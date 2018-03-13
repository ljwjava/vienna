package lerrain.service.env;

import com.alibaba.fastjson.JSON;
import lerrain.service.env.source.SourceMgr;
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

                c.setInit(Script.scriptOf(m.getString("init")));

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

            c.setStack(stack);
        }

        for (Environment c : r)
        {
            if (c.getRefer() != null) for (String code : c.getRefer())
                c.getStack().declare(code, map.get(code).getStack());

            try
            {
                initEnv(c);
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

                Log.debug("loading env's const... " + consName);
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
                    else if (type == 8)
                        val = jdbc.queryForList(valstr);

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
                boolean lockEnv = "Y".equalsIgnoreCase(rs.getString("lock_env"));

                Log.debug("loading env's function... " + consName);

                try
                {
                    Function f = new InnerFunction(p.getCode() + "/" + consName, Script.scriptOf(script), Common.isEmpty(params) ? null : params.split(","), lockEnv ? p.getStack() : null);

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
                        p.putVar(fe, f);
                }
                catch (Exception e)
                {
                    Log.error(e);
                }
            }

        });

        if (p.getInit() != null)
            p.getInit().run(p.getStack());
    }

    public static class InnerFunction implements Function
    {
        String name;
        Formula f;
        String[] params;
        Factors fixed;

        public InnerFunction(String name, Formula f, String[] params, Factors fixed)
        {
            this.name = name;
            this.f = f;
            this.params = params;
            this.fixed = fixed;
        }

        @Override
        public Object run(Object[] objects, Factors factors)
        {
            Stack s = new Stack(fixed == null ? factors : fixed);
            s.declare("STACK_FUNCTION", name);

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
