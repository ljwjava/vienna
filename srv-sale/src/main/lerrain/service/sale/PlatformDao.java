package lerrain.service.sale;

import com.alibaba.fastjson.JSON;
import lerrain.service.common.Log;
import lerrain.service.sale.function.*;
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
        return jdbc.query("select * from t_platform where valid is null order by name", new RowMapper<Platform>()
        {
            @Override
            public Platform mapRow(ResultSet m, int rowNum) throws SQLException
            {
                Platform c = new Platform();
                c.setId(m.getLong("id"));
                c.setCode(m.getString("code"));
                c.setName(m.getString("name"));

//                c.setPerform(Script.scriptOf(m.getString("perform")));
//                c.setVerify(Script.scriptOf(m.getString("verify")));
//                c.setApply(Script.scriptOf(m.getString("apply")));
//                c.setCallback(Script.scriptOf(m.getString("callback")));

                c.setCreateTime(m.getTimestamp("create_time"));
                c.setUpdateTime(m.getTimestamp("update_time"));

                Stack stack = new Stack();
                stack.set("platformId", c.getId());
                stack.set("platformCode", c.getCode());
                stack.set("platformName", c.getName());
                stack.setAll(funcs);
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

                Stack s = p.getEnv();

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
                    val = Script.scriptOf(valstr).run(s);

                String[] name = consName.split("[.]");
                if (name.length == 1)
                {
                    s.set(name[0], val);
                }
                else if (name.length == 2)
                {
                    Map map = (Map)s.get(name[0]);
                    if (map == null)
                    {
                        map = new HashMap();
                        s.set(name[0], map);
                    }
                    map.put(name[1], val);
                }
            }

        }, p.getId());

        jdbc.query("select * from t_platform_function where platform_id = ?", new RowCallbackHandler()
        {
            @Override
            public void processRow(ResultSet rs) throws SQLException
            {
                String consName = rs.getString("name");
                String params = rs.getString("params");
                String script = rs.getString("script");

                Log.debug("loading... " + consName);

                Function f = new SelfFunction(Script.scriptOf(script), Common.isEmpty(params) ? null : params.split(","));

                Stack s = p.getEnv();
                String[] name = consName.split("[.]");
                if (name.length == 1)
                {
                    s.set(name[0], f);
                }
                else if (name.length == 2)
                {
                    Map map = (Map) s.get(name[0]);
                    if (map == null)
                    {
                        map = new HashMap();
                        s.set(name[0], map);
                    }
                    map.put(name[1], f);
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

                Log.debug("loading... " + name);
                map.put(name, Script.scriptOf(script));
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
                for (int i = 0; i < params.length && i < objects.length; i++)
                    s.set(params[i], objects[i]);

            return f.run(s);
        }
    }
}
