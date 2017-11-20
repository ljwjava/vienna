package lerrain.service.biz;

import lerrain.tool.Common;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class DevelopDao
{
    @Autowired JdbcTemplate jdbc;

    public List<Map> loadFunctionList(Long envId)
    {
        return jdbc.query("select a.* from t_env_function a where env_id = ?", new Object[] {envId}, new RowMapper<Map>()
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

                return map;
            }
        });
    }

    public Map loadFunction(Long funcId)
    {
        return jdbc.queryForObject("select a.*, b.url, b.post_json, b.script as develop from t_env_function a left join t_env_function_test b on a.id = b.function_id where a.id = ?", new Object[] {funcId}, new RowMapper<Map>()
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
                map.put("remark", rs.getString("remark"));

                map.put("url", rs.getString("url"));
                map.put("postJson", rs.getString("post_json"));
                map.put("develop", rs.getString("develop"));

                return map;
            }
        });
    }

    public void save(Long functionId, String name, String params, String script, String reqUrl, String reqJson)
    {
        if (jdbc.queryForObject("select exists(select id from t_env_function_test where function_id = ?) from dual", new Object[] {functionId}, Integer.TYPE) > 0)
            jdbc.update("update t_env_function_test set name = ?, params = ?, script = ?, url = ?, post_json = ? where function_id = ?", new Object[] {name, params, script, reqUrl, reqJson, functionId});
        else
            jdbc.update("insert into t_env_function_test(function_id, name, params, script, url, post_json) values(?,?,?,?,?,?)", new Object[] {functionId, name, params, script, reqUrl, reqJson});
    }
}
