package lerrain.service.data;

import lerrain.service.env.EnvService;
import lerrain.tool.script.Script;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class TaskDao
{
    @Autowired
    JdbcTemplate jdbc;

    public List<Task> loadAllTask(final EnvService envSrv)
    {
        return jdbc.query("select * from t_task where valid is null order by sequence", new RowMapper<Task>()
        {
            @Override
            public Task mapRow(ResultSet m, int rowNum) throws SQLException
            {
                Long envId = m.getLong("env_id");
                String script = m.getString("script");
                String invoke = m.getString("invoke");

                Task task = new Task();
                task.setStack(envSrv.getEnv(envId).getStack());
                task.setScript(Script.scriptOf(script));
                task.setInvoke(invoke);

                return task;
            }
        });
    }
}
