package lerrain.service.underwriting;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lerrain.tool.script.Script;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    public void save(Underwriting uw)
    {
        JSONObject quests = new JSONObject();
        String answer = JSON.toJSONString(uw.val);
        String result = JSON.toJSONString(uw.res);

        synchronized (uw)
        {
            for (Map.Entry<Integer, List<Quest>> e : uw.quest.entrySet())
            {
                JSONArray ja = new JSONArray();
                for (Quest q : e.getValue())
                    ja.add(q.getCode());

                quests.put(e.getKey().toString(), ja);
            }
        }

        String sql = "replace into t_uw_answer(id, quests, answer, result) values(?, ?, ?, ?)";
        jdbc.update(sql, uw.getId(), quests.toJSONString(), answer, result);
    }

    public Underwriting load(Long uwId, final Map<String, Quest> qm)
    {
        String sql = "select * from t_uw_answer where id = ?";
        return jdbc.queryForObject(sql, new RowMapper<Underwriting>()
        {
            @Override
            public Underwriting mapRow(ResultSet rs, int rowNum) throws SQLException
            {
                Underwriting uw = new Underwriting();
                uw.setId(rs.getLong("id"));

                JSONObject val = JSON.parseObject(rs.getString("answer"));
                if (val != null) for (String key : val.keySet())
                    uw.setAnswer(Integer.parseInt(key), val.getJSONObject(key));

                JSONObject res = JSON.parseObject(rs.getString("result"));
                if (res != null) for (String key : res.keySet())
                    uw.setResult(Integer.parseInt(key), (char)res.getIntValue(key));

                JSONObject qst = JSON.parseObject(rs.getString("quests"));
                if (qst != null) for (String key : qst.keySet())
                {
                    JSONArray list = qst.getJSONArray(key);
                    List<Quest> qlist = new ArrayList<>();
                    for (int i=0;i<list.size();i++)
                        qlist.add(qm.get(list.getString(i)));
                    uw.setQuests(Integer.parseInt(key), qlist);
                }

                return uw;
            }
        }, uwId);
    }
}
