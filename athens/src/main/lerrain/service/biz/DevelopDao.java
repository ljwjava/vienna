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

    public void apply(Long gatewayId, String script)
    {
        backup("t_gateway", "script", gatewayId);
        jdbc.update("update t_gateway set script = ? where id = ?", script, gatewayId);
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

    public List<Map> loadGatewayList()
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

    public String loadTesting(String url)
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

    public void save(String url, String param)
    {
        jdbc.update("replace into t_test(`key`, `value`) values(?, ?)", new Object[] {url, param});
    }
}
