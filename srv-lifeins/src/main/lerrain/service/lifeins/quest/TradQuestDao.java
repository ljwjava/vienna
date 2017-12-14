package lerrain.service.lifeins.quest;

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
public class TradQuestDao
{
    @Autowired
    JdbcTemplate jdbc;

    String sql = "select b.*, a.condition as rc from t_quest_relation a, t_quest b where a.quest_id = b.id and a.company = ? and a.apply_type = ? and a.customer_type = ? order by sequence";

    public List<TradQuest> getQuests(String company, int applyType, int customerType)
    {
        return jdbc.query(sql, new Object[]{company, applyType, customerType}, new RowMapper<TradQuest>()
        {
            @Override
            public TradQuest mapRow(ResultSet m, int arg1) throws SQLException
            {
                TradQuest q = new TradQuest();
                q.setId(m.getLong("id"));
                q.setCode(m.getString("code"));
                q.setType(m.getInt("type"));

                String formula = m.getString("rc");
                if (formula == null)
                    formula = m.getString("condition");
                q.setCondition(Script.scriptOf(formula));

                int format = m.getInt("format");
                String text = m.getString("quest");
                q.setDetail(format == 2 ? JSON.parseObject(text) : format == 3 ? JSON.parseArray(text) : null);

                return q;
            }
        });
    }
}
