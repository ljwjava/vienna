package lerrain.service.biz;

import lerrain.tool.Common;
import lerrain.tool.script.Script;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class DevelopDao
{
    @Autowired JdbcTemplate jdbc;

    public void save(Long gatewayId, Long envId, String uri, int type, String forwardTo, String script, boolean login, String with, Integer seq, String remark)
    {
        jdbc.update("replace into t_gateway (id, env_id, uri, `type`, support_get, support_post, forward, forward_to, script, login, `with`, seq, remark) values(?, ?, ?, ?, 'Y', 'Y', ?, ?, ?, ?, ?, ?, ?)", gatewayId, envId, uri, type, forwardTo == null ? 0 : 1, forwardTo, script, login ? "Y" : "N", with, seq, remark);
    }

    public void apply(Long functionId, String name, String params, String script)
    {
        backup("t_env_function", "script", functionId);
        jdbc.update("update t_env_function set name = ?, params = ?, script = ? where id = ?", name, params, script, functionId);
    }

    public void backup(String table, String column, Object seek)
    {
        String sql = String.format("insert into t_script_history (script, table_name, column_name, seek, del_time) values ((select %s from %s where id = ?), ?, ?, ?, now())", column, table);
        jdbc.update(sql, seek, table, column, seek);
    }

    public Long saveGateway(Map m)
    {
        Long gatewayId = Common.toLong(m.get("gatewayId"));
        return null;
    }

    public Long nextGatewayId()
    {
        String sql = "select max(id) from t_gateway";
        return jdbc.queryForObject(sql, Long.class);
    }

    public Map viewGateway(Long gatewayId)
    {
        String sql = "select t.* from t_gateway t where t.id = ?";
        return jdbc.queryForObject(sql, new RowMapper<Map>()
        {
            @Override
            public Map mapRow(ResultSet rs, int rowNum) throws SQLException
            {
                Map map = new HashMap();
                map.put("id", rs.getLong("id"));
                map.put("envId", rs.getLong("env_id"));
                map.put("uri", rs.getString("uri"));
                map.put("type", Common.intOf(rs.getInt("type"), 0));
                map.put("login", rs.getString("login"));
                map.put("remark", rs.getString("remark"));
                map.put("sequence", rs.getString("seq"));
                map.put("script", rs.getString("script"));

                return map;
            }
        }, gatewayId);
    }

    public List<Map> listGateway(int from, int number)
    {
        String sql = "select t.* from t_gateway t where t.valid is null order by t.uri limit ?, ?";
        return jdbc.query(sql, new RowMapper<Map>()
        {
            @Override
            public Map mapRow(ResultSet rs, int rowNum) throws SQLException
            {
                Map map = new HashMap();
                map.put("id", rs.getLong("id"));
                map.put("envId", rs.getLong("env_id"));
                map.put("uri", rs.getString("uri"));
                map.put("type", Common.intOf(rs.getInt("type"), 0));
                map.put("login", rs.getString("login"));
                map.put("remark", rs.getString("remark"));
                //map.put("script", rs.getString("script"));

                return map;
            }
        }, from, number);
    }

    public int count()
    {
        String sql = "select count(*) from t_gateway where valid is null";
        return jdbc.queryForObject(sql, Integer.class);
    }

    public List<Map> queryGateway()
    {
        String sql = "select * from t_gateway where valid is null order by uri";
        return jdbc.query(sql, new RowMapper<Map>()
        {
            @Override
            public Map mapRow(ResultSet rs, int rowNum) throws SQLException
            {
                Map map = new HashMap();
                map.put("id", rs.getLong("id"));
                map.put("uri", rs.getString("uri"));
                map.put("script", rs.getString("script"));
                map.put("envId", rs.getLong("env_id"));

                return map;
            }
        });
    }

    public String loadCache(String url)
    {
        try
        {
            String sql = "select `value` from t_test where `key` = ?";
            return jdbc.queryForObject(sql, new Object[]{url}, String.class);
        }
        catch (Exception e)
        {
            return null;
        }
    }

    public List<Map> loadFunctionList(Long envId)
    {
        return jdbc.query("select a.* from t_env_function a where env_id = ?", new Object[]{envId}, new RowMapper<Map>()
        {
            @Override
            public Map mapRow(ResultSet rs, int rowNum) throws SQLException
            {
                String name = rs.getString("name");
                String otherNames = rs.getString("other_names");

                Map map = new HashMap();
                map.put("id", rs.getLong("id"));
                map.put("name", name);
                map.put("aka", otherNames);
                map.put("params", rs.getString("params"));
                map.put("script", rs.getString("script"));

                return map;
            }
        });
    }

    public Map loadFunction(Long funcId)
    {
        return jdbc.queryForObject("select a.* from t_env_function a where a.id = ?", new Object[]{funcId}, new RowMapper<Map>()
        {
            @Override
            public Map mapRow(ResultSet rs, int rowNum) throws SQLException
            {
                String name = rs.getString("name");
                String otherNames = rs.getString("other_names");

                Map map = new HashMap();
                map.put("id", rs.getLong("id"));
                map.put("envId", rs.getLong("env_id"));
                map.put("name", name);
                map.put("aka", otherNames);
                map.put("params", rs.getString("params"));
                map.put("script", rs.getString("script"));
                map.put("remark", rs.getString("remark"));

                return map;
            }
        });
    }

    /*
    public void save(Long functionId, String name, String params, String script, String reqUrl, String reqJson)
    {
        if (jdbc.queryForObject("select exists(select id from t_env_function_test where function_id = ?) from dual", new Object[] {functionId}, Integer.TYPE) > 0)
            jdbc.update("update t_env_function_test set name = ?, params = ?, script = ?, url = ?, post_json = ? where function_id = ?", new Object[] {name, params, script, reqUrl, reqJson, functionId});
        else
            jdbc.update("insert into t_env_function_test(function_id, name, params, script, url, post_json) values(?,?,?,?,?,?)", new Object[] {functionId, name, params, script, reqUrl, reqJson});
    }
    */

    public void saveCache(String url, String param)
    {
        jdbc.update("replace into t_test(`key`, `value`) values(?, ?)", new Object[] {url, param});
    }

    public void removeCache(String url)
    {
        jdbc.update("delete from t_test where `key` = ?", url);
    }
}
