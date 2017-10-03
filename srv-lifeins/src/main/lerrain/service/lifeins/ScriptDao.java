package lerrain.service.lifeins;

import lerrain.project.insurance.plan.Plan;
import lerrain.tool.formula.Formula;
import lerrain.tool.script.Script;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lerrain on 2017/5/4.
 */
public class ScriptDao
{
    @Autowired
    JdbcTemplate jdbc;

    Map<Long, Formula> script;

    private synchronized void init()
    {
        if (script != null)
            return;

        script = new HashMap<>();

        String sql = "select * from t_script";
        jdbc.queryForObject("select * from t_script", new RowMapper<Object>()
        {
            public Object mapRow(ResultSet m, int arg1) throws SQLException
            {
                script.put(m.getLong("id"), Script.scriptOf(m.getString("script")));
                return null;
            }
        });
    }

    public Formula getScript(Long id)
    {
        if (script == null)
            init();

        return script.get(id);
    }
}
