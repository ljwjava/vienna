package lerrain.service.lifeins;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lerrain.project.insurance.plan.Plan;
import lerrain.project.insurance.product.Insurance;
import lerrain.service.common.Log;
import lerrain.service.lifeins.LifeinsService;
import lerrain.service.lifeins.LifeinsUtil;
import lerrain.tool.Common;
import lerrain.tool.formula.Formula;
import lerrain.tool.script.Script;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class PlanDao
{
    @Autowired
    JdbcTemplate jdbc;

    @Autowired
    LifeinsService lifeins;

    public void save(Plan plan)
    {
        Date now = new Date();
        JSONObject json;

        synchronized (plan)
        {
            json = LifeinsUtil.toSaveJson(plan);
        }

        String sql = "replace into t_ins_plan(id, plan, update_time) values(?, ?, ?)";
        jdbc.update(sql, plan.getId(), json.toJSONString(), now);
    }

    public Plan load(String planId)
    {
        String sql = "select plan from t_ins_plan where id = ? and valid is null";
        String res = jdbc.queryForObject(sql, new Object[]{planId}, String.class);

        return LifeinsUtil.toPlan(lifeins, JSON.parseObject(res), planId);
    }

    public Map loadAllScript()
    {
        final Map<Object, Formula> m = new HashMap<>();

        jdbc.query("select * from t_ins_perform", new RowCallbackHandler()
        {
            @Override
            public void processRow(ResultSet rs) throws SQLException
            {
                Long id = rs.getLong("id");
                String opt1Name = rs.getString("opt1_name");
                String opt1 = rs.getString("opt1");
                String opt2Name = rs.getString("opt2_name");
                String opt2 = rs.getString("opt2");
                String opt3Name = rs.getString("opt3_name");
                String opt3 = rs.getString("opt3");

                m.put(id, Script.scriptOf(rs.getString("create")));
                if (!Common.isEmpty(opt1Name))
                    m.put(id + "/" + opt1Name, Script.scriptOf(opt1));
                if (!Common.isEmpty(opt2Name))
                    m.put(id + "/" + opt2Name, Script.scriptOf(opt2));
                if (!Common.isEmpty(opt3Name))
                    m.put(id + "/" + opt3Name, Script.scriptOf(opt3));
            }
        });

        return m;
    }
}
