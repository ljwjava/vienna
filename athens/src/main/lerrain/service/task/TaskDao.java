package lerrain.service.task;

import lerrain.service.env.EnvService;
import lerrain.service.env.Environment;
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

    public List<TimingTask> loadAllTask(final EnvService envSrv)
    {
        return jdbc.query("select * from t_task where valid is null order by sequence", new RowMapper<TimingTask>()
        {
            @Override
            public TimingTask mapRow(ResultSet m, int rowNum) throws SQLException
            {
                Long envId = m.getLong("env_id");
                String script = m.getString("script");
                String invoke = m.getString("invoke");

                if (!envSrv.isValid(envId))
                    return null;

                TimingTask task = new TimingTask();
                task.setStack(envSrv.getEnv(envId).getStack());
                task.setScript(Script.scriptOf(script));
                task.setInvoke(invoke);

                return task;
            }
        });
    }
}
