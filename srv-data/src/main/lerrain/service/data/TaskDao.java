package lerrain.service.data;

import lerrain.service.common.Log;
import lerrain.tool.script.Script;
import lerrain.tool.script.Stack;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class TaskDao
{
    @Autowired JdbcTemplate jdbc;

    public Map<Long, Task> loadAllTask()
    {
        final Map<Long, Task> map = new HashMap<>();

        jdbc.query("select * from t_task order by env_id", new RowCallbackHandler()
        {
            @Override
            public void processRow(ResultSet rs) throws SQLException
            {
                Task task = new Task();
                task.setId(rs.getLong("id"));
                task.setEnvId(rs.getLong("env_id"));
                task.setCode(rs.getString("code"));
                task.setInvoke(rs.getString("invoke"));

                try
                {
                    task.setScript(Script.scriptOf(rs.getString("script")));
                }
                catch (Exception e)
                {
                    Log.error(task.getCode() + " load fail");
                    e.printStackTrace();
                }

                map.put(task.getId(), task);
            }
        });

        return map;
    }
}
