package lerrain.service.underwriting;

import com.alibaba.fastjson.JSON;
import lerrain.tool.script.Script;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class UnderwritingDao
{
    @Autowired JdbcTemplate jdbc;

    public List<Quest> listAll()
    {
        return jdbc.query("select * from t_uw_quest order by code", new RowMapper<Quest>()
        {
            @Override
            public Quest mapRow(ResultSet m, int arg1) throws SQLException
            {
                return questOf(m);
            }
        });
    }

    private Quest questOf(ResultSet m) throws SQLException
    {
        Quest p = new Quest();
        p.setCode(m.getString("code"));
        p.setCondition(Script.scriptOf(m.getString("condition")));
        p.setDisease(m.getString("disease"));
        p.setType(m.getInt("type"));
        p.setText(m.getString("text"));
        p.setWidget(m.getInt("widget"));
        p.setFeature(m.getString("feature"));
        p.setAnswer(JSON.parse(m.getString("answer")));
        p.setNext(m.getString("next"));

        return p;
    }
}
