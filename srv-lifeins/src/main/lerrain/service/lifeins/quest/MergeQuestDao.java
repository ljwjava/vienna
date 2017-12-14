package lerrain.service.lifeins.quest;

import com.alibaba.fastjson.JSON;
import lerrain.project.insurance.product.Insurance;
import lerrain.service.common.Log;
import lerrain.service.lifeins.LifeinsService;
import lerrain.tool.script.Script;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lerrain on 2017/5/21.
 */
@Repository
public class MergeQuestDao
{
    @Autowired
    JdbcTemplate jdbc;

    @Autowired
    LifeinsService lifeSrv;

    public Map<String, Object> loadQuestConst()
    {
        final Map<String, Object> map = new HashMap<>();

        jdbc.query("select * from t_ins_quest_const", new RowCallbackHandler()
        {
            @Override
            public void processRow(ResultSet rs) throws SQLException
            {
                String name = rs.getString("name");
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
                        val = "Y".equalsIgnoreCase(valstr) || "true".equalsIgnoreCase(valstr);

                    map.put(name, val);
                }
                catch (Exception e)
                {
                    Log.error(e);
                }
            }
        });

        return map;
    }

    public void loadClauseFactors()
    {
        jdbc.query("select * from t_ins_quest_clause", new RowCallbackHandler()
        {
            @Override
            public void processRow(ResultSet rs) throws SQLException
            {
                String clause = rs.getString("clause");
                Insurance ins = lifeSrv.getProduct(clause);

                if (ins == null)
                {
                    Log.alert(clause + " not found!");
                    return;
                }

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
                        val = "Y".equalsIgnoreCase(valstr) || "true".equalsIgnoreCase(valstr);

                    String code = rs.getString("code");
                    ins.setAdditional(code, true);

                    String name = rs.getString("name");
                    if (name != null)
                        ins.setAdditional(code + "_" + name, val);
                }
                catch (Exception e)
                {
                    Log.error(e);
                }
            }
        });
    }

    public List<MergeQuest> loadAllQuests()
    {
        return jdbc.query("select * from t_ins_quest order by code", new RowMapper<MergeQuest>()
        {
            @Override
            public MergeQuest mapRow(ResultSet rs, int i) throws SQLException
            {
                MergeQuest quest = new MergeQuest();
                quest.setCode(rs.getString("code"));
                quest.setText(rs.getString("text").trim());
                quest.setCondition(Script.scriptOf(rs.getString("condition")));

                final Map map = new HashMap();
                jdbc.query("select * from t_ins_quest_factor where code = ?", new Object[] {quest.getCode()}, new RowCallbackHandler()
                {
                    @Override
                    public void processRow(ResultSet rs) throws SQLException
                    {
                        String name = rs.getString("name");
                        map.put(name, Script.scriptOf(rs.getString("script")));
                    }
                });
                quest.setVars(map);

                return quest;
            }
        });
    }
}
