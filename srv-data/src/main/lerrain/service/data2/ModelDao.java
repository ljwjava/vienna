package lerrain.service.data2;

import lerrain.service.common.Log;
import lerrain.tool.script.Script;
import lerrain.tool.script.Stack;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Repository
public class ModelDao
{
    @Autowired JdbcTemplate jdbc;

    public List<Model> loadAllModel(final Map<Object, Stack> envMap)
    {
        return jdbc.query("select * from t_model order by env_id", new RowMapper<Model>()
        {
            @Override
            public Model mapRow(ResultSet rs, int rowNum) throws SQLException
            {
                Model script = new Model();
                script.setId(rs.getLong("id"));
                script.setEnv(envMap.get(rs.getLong("env_id")));
                script.setSortId(rs.getLong("sort_id"));
                script.setName(rs.getString("name"));

                try
                {
                    script.setScript(Script.scriptOf(rs.getString("script")));
                }
                catch (Exception e)
                {
                    Log.error(script.getName() + " load fail");
                    e.printStackTrace();
                }

                return script;
            }
        });
    }
}
