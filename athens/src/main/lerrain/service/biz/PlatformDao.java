package lerrain.service.biz;

import com.alibaba.fastjson.JSON;
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
public class PlatformDao
{
    @Autowired JdbcTemplate jdbc;

    public List<Platform> loadChannels(final Map funcs)
    {
        final Map<Long, Platform> map = new HashMap<>();

        return jdbc.query("select * from t_platform where valid is null order by parent_id, name", new RowMapper<Platform>()
        {
            @Override
            public Platform mapRow(ResultSet m, int rowNum) throws SQLException
            {
                Platform c = new Platform();
                c.setId(m.getLong("id"));
                c.setCode(m.getString("code"));
                c.setName(m.getString("name"));

                c.setCreateTime(m.getTimestamp("create_time"));
                c.setUpdateTime(m.getTimestamp("update_time"));

                Stack stack;
                Long parentId = Common.toLong(m.getObject("parent_id"));

                if (parentId == null)
                {
                    stack = new Stack();
                    stack.setAll(funcs);
                }
                else
                {
                    stack = new Stack(map.get(parentId).getEnv());
                }

                stack.set("platformId", c.getId());
                stack.set("platformCode", c.getCode());
                stack.set("platformName", c.getName());
                c.setEnv(stack);

                try
                {
                    Script script = Script.scriptOf(m.getString("env"));
                    if (script != null)
                        script.run(c.getEnv());

                    initPlatform(c);
                }
                catch (Exception e)
                {
                    Log.error(e);
                }

                map.put(c.getId(), c);
                return c;
            }
        });
    }

    private void initPlatform(final Platform p)
    {
        jdbc.query("select * from t_platform_env where platform_id = ?", new RowCallbackHandler()
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
                        val = Script.scriptOf(valstr).run(p.getEnv());

                    Log.debug("loading env... " + consName);
                    p.putVar(consName, val);
                }
                catch (Exception e)
                {
                    Log.error(e);
                }
            }

        }, p.getId());

        jdbc.query("select * from t_platform_function where platform_id = ?", new RowCallbackHandler()
        {
            @Override
            public void processRow(ResultSet rs) throws SQLException
            {
                String consName = rs.getString("name");
                String otherNames = rs.getString("other_names");
                String params = rs.getString("params");
                String script = rs.getString("script");

                try
                {
                    Function f = new SelfFunction(Script.scriptOf(script), Common.isEmpty(params) ? null : params.split(","));

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
                        Log.debug("loading function... " + fe);
                        p.putVar(fe, f);
                    }
                }
                catch (Exception e)
                {
                    Log.error(e);
                }
            }

        }, p.getId());

        final Map<String, Formula> map = new HashMap();

        jdbc.query("select * from t_platform_operate where platform_id = ?", new RowCallbackHandler()
        {
            @Override
            public void processRow(ResultSet rs) throws SQLException
            {
                String name = rs.getString("name");
                String script = rs.getString("script");

                try
                {
                    Log.debug("loading... " + name);
                    map.put(name, Script.scriptOf(script));
                }
                catch (Exception e)
                {
                    Log.error(e);
                }
            }

        }, p.getId());

        p.setScripts(map);
    }

    public String loadEnvScript(Long platformId)
    {
        String sql = "select env from t_platform where id = ?";
        return jdbc.queryForObject(sql, String.class, platformId);
    }

    public void saveEnvScript(Long platformId, String env)
    {
        backupScript("t_platform", "env", platformId);

        String sql = "update t_platform set env = ? where id = ?";
        jdbc.update(sql, env, platformId);
    }

    public String loadPerformScript(Long platformId)
    {
        String sql = "select perform from t_platform where id = ?";
        return jdbc.queryForObject(sql, String.class, platformId);
    }

    public void savePerformScript(Long platformId, String perform)
    {
        backupScript("t_platform", "perform", platformId);

        String sql = "update t_platform set perform = ? where id = ?";
        jdbc.update(sql, perform, platformId);
    }

    public void backupScript(String table, String column, Object seek)
    {
//        String sql = String.format("select %s from %s where id = ?", column, table);
//        String script = jdbc.queryForObject(sql, String.class, seek);
//
//        String s1 = "insert into t_script_history (script, table_name, column_name, seek, del_time) values (?, ?, ?, ?, now())";
//        jdbc.update(s1, script, table, column, seek);

        String sql = String.format("insert into t_script_history (script, table_name, column_name, seek, del_time) values ((select %s from %s where id = ?), ?, ?, ?, now())", column, table);
        jdbc.update(sql, seek, table, column, seek);
    }

    private static class SelfFunction implements Function
    {
        Formula f;
        String[] params;

        public SelfFunction(Formula f, String[] params)
        {
            this.f = f;
            this.params = params;
        }

        @Override
        public Object run(Object[] objects, Factors factors)
        {
            Stack s = new Stack(factors);
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
