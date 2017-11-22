package lerrain.service.biz;

import com.alibaba.fastjson.JSON;
import lerrain.service.biz.source.SourceMgr;
import lerrain.service.common.Log;
import lerrain.tool.Common;
import lerrain.tool.formula.Factors;
import lerrain.tool.formula.Formula;
import lerrain.tool.formula.Function;
import lerrain.tool.script.Script;
import lerrain.tool.script.Stack;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
public class EnvDao
{
    @Autowired
    JdbcTemplate jdbc;

    @Autowired
    SourceMgr sourceMgr;

    public void loadDbSource()
    {
        sourceMgr.close();

        jdbc.query("select * from t_data_source", new RowCallbackHandler()
        {
            @Override
            public void processRow(ResultSet rs) throws SQLException
            {
                Long id = rs.getLong("id");
                int type = rs.getInt("type");
                Map opts = JSON.parseObject(rs.getString("config"));

                sourceMgr.add(id, type, opts);
            }
        });
    }

    public List<Environment> loadAllEnv(final Map funcs)
    {
        final Map<String, Environment> map = new HashMap<>();

        List<Environment> r = jdbc.query("select * from t_env where valid is null order by parent_id, name", new RowMapper<Environment>()
        {
            @Override
            public Environment mapRow(ResultSet m, int rowNum) throws SQLException
            {
                Environment c = new Environment();
                c.setId(m.getLong("id"));
                c.setCode(m.getString("code"));
                c.setName(m.getString("name"));
                c.setParentId(Common.toLong(m.getObject("parent_id")));

                String refer = m.getString("refer");
                c.setRefer(refer == null ? null : refer.split(","));

                c.setCreateTime(m.getTimestamp("create_time"));
                c.setUpdateTime(m.getTimestamp("update_time"));

                map.put(c.getId().toString(), c);
                map.put(c.getCode(), c);

                return c;
            }
        });

        for (Environment c : r)
        {
            Stack stack;

            if (c.getParentId() == null)
            {
                stack = new Stack();
                stack.setAll(funcs);
            }
            else
            {
                stack = new Stack(map.get(c.getParentId().toString()).getStack());
            }

            stack.declare("ENV_ID", c.getId());
            stack.declare("ENV_CODE", c.getCode());
            stack.declare("ENV_NAME", c.getName());

            if (c.getRefer() != null) for (String code : c.getRefer())
                stack.declare(code, map.get(code).getStack());

            c.setStack(stack);

            try
            {
                initEnv(c);

//                Script script = Script.scriptOf(m.getString("stack"));
//                if (script != null)
//                    script.run(c.getStack());
            }
            catch (Exception e)
            {
                Log.error(e);
            }
        }

        return r;
    }

    private void initEnv(final Environment p)
    {
        jdbc.query("select * from t_env_ds where env_id = ?", new Object[] {p.getId()}, new RowCallbackHandler()
        {
            @Override
            public void processRow(ResultSet rs) throws SQLException
            {
                String anchor = rs.getString("anchor");
                Long sourceId = rs.getLong("source_id");

                p.getStack().set(anchor, sourceMgr.getSource(sourceId));
            }
        });

        jdbc.query("select * from t_env_const where env_id = ? or env_code = ?", new Object[] {p.getId(), p.getCode()}, new RowCallbackHandler()
        {
            @Override
            public void processRow(ResultSet rs) throws SQLException
            {
                String consName = rs.getString("name");
                String valstr = rs.getString("value");
                int type = rs.getInt("type");

                try
                {
                    Object val = null;
                    if (type == 1)
                        val = valstr;
                    else if (type == 2)
                        val = new BigDecimal(valstr);
                    else if (type == 3)
                        val = Integer.valueOf(valstr);
                    else if (type == 4)
                        val = JSON.parseObject(valstr);
                    else if (type == 5)
                        val = JSON.parseArray(valstr);
                    else if (type == 6)
                        val = Script.scriptOf(valstr).run(p.getStack());
                    else if (type == 7)
                        val = "Y".equalsIgnoreCase(valstr);

                    Log.debug("loading env's const... " + consName);
                    p.putVar(consName, val);
                }
                catch (Exception e)
                {
                    Log.error(e);
                }
            }

        });

        jdbc.query("select * from t_env_function where env_id = ? or env_code = ?", new Object[] {p.getId(), p.getCode()}, new RowCallbackHandler()
        {
            @Override
            public void processRow(ResultSet rs) throws SQLException
            {
                String consName = rs.getString("name");
                String otherNames = rs.getString("other_names");
                String params = rs.getString("params");
                String script = rs.getString("script");
                String envCode = rs.getString("env_code"); //有envCode的绑定到自己环境，外部调用执行时，其环境仍然是自己的

                try
                {
                    Function f = new InnerFunction(Script.scriptOf(script), Common.isEmpty(params) ? null : params.split(","), envCode == null ? null : p.getStack());

                    String[] allName;
                    if (Common.isEmpty(otherNames))
                    {
                        allName = new String[]{consName};
                    }
                    else
                    {
                        String[] append = otherNames.split(",");
                        allName = Arrays.copyOf(append, append.length + 1);
                        allName[allName.length - 1] = consName;
                    }

                    for (String fe : allName)
                    {
                        Log.debug("loading env's function... " + fe);
                        p.putVar(fe, f);
                    }
                }
                catch (Exception e)
                {
                    Log.error(e);
                }
            }

        });
    }

    public static class InnerFunction implements Function
    {
        Formula f;
        String[] params;
        Factors fixed;

        public InnerFunction(Formula f, String[] params, Factors fixed)
        {
            this.f = f;
            this.params = params;
            this.fixed = fixed;
        }

        @Override
        public Object run(Object[] objects, Factors factors)
        {
            Stack s = new Stack(fixed == null ? factors : fixed);
            if (params != null && objects != null)
            {
                for (int i = 0; i < params.length && i < objects.length; i++)
                {
                    s.declare(params[i]);
                    s.set(params[i], objects[i]);
                }
            }

            return f.run(s);
        }
    }
}
