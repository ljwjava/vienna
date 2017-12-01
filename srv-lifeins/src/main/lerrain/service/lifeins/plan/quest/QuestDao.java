package lerrain.service.lifeins.plan.quest;

import com.alibaba.fastjson.JSON;
import lerrain.project.insurance.product.Insurance;
import lerrain.service.common.Log;
import lerrain.service.lifeins.LifeinsService;
import lerrain.service.lifeins.Quest;
import lerrain.tool.script.Script;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by lerrain on 2017/5/21.
 */
@Service
public class QuestDao
{
    @Autowired
    JdbcTemplate jdbc;

    @Autowired
    LifeinsService lifeSrv;

    public void loadClauseFactors()
    {
        jdbc.query("select * from t_ins_quest_clause", new RowCallbackHandler()
        {
            @Override
            public void processRow(ResultSet rs) throws SQLException
            {
                String clause = rs.getString("clause");
                Insurance ins = lifeSrv.getProduct(clause);

                String valstr = rs.getString("value");
                int type = rs.getInt("type");

                try
                {
                    Object val = null;
                    if (type == 1)
                        val = valstr;
                    else if (type == 2)
                        val = new BigDecimal(valstr);
                    else if (type == 3)
                        val = Integer.valueOf(valstr);
                    else if (type == 4)
                        val = JSON.parseObject(valstr);
                    else if (type == 5)
                        val = JSON.parseArray(valstr);
                    else if (type == 7)
                        val = "Y".equalsIgnoreCase(valstr);

                    ins.setAdditional(rs.getString("code"), val);
                }
                catch (Exception e)
                {
                    Log.error(e);
                }
            }
        });

        jdbc.query("select * from t_ins_quest_factors", new RowCallbackHandler()
        {
            @Override
            public void processRow(ResultSet rs) throws SQLException
            {
                String name = rs.getString("name");
                int mode = rs.getInt("mode");
            }
        });
    }

    public List<QuestDefine> loadAllQuests()
    {
        return jdbc.query("select * from t_ins_quest_define", new RowMapper<QuestDefine>()
        {
            @Override
            public QuestDefine mapRow(ResultSet rs, int i) throws SQLException
            {
                QuestDefine quest = new QuestDefine();
                quest.setCode(rs.getString("code"));
                quest.setText(rs.getString("text"));
                quest.setCondition(Script.scriptOf(rs.getString("condition")));

                return quest;
            }
        });
    }
}
