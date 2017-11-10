package lerrain.service.lifeins.plan;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lerrain.project.insurance.plan.Plan;
import lerrain.service.lifeins.LifeinsService;
import lerrain.service.lifeins.LifeinsUtil;
import lerrain.tool.Common;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Date;

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
        JSONObject json = LifeinsUtil.toSaveJson(plan);

        if (exists(plan.getId()))
        {
            String sql = "update t_life_plan set content = ?, update_time = ? where id = ?";
            jdbc.update(sql, json.toJSONString(), now, plan.getId());
        }
        else
        {
            String sql = "insert into t_life_plan(id, content, creator, create_time, updater, update_time) values(?, ?, ?, ?, ?, ?)";
            jdbc.update(sql, plan.getId(), json.toJSONString(), 1, now, 1, now);
        }
    }

    public boolean exists(String planId)
    {
        if (Common.isEmpty(planId))
            return false;

        String sql = "select exists(*) from t_life_plan where id = ? and valid is null";
        return jdbc.queryForObject(sql, new Object[]{planId}, Boolean.class);
    }

    public boolean delete(String planId)
    {
        String sql = "update t_life_plan set valid = 'N', update_time = ? where id = ?";
        jdbc.update(sql, new Date(), planId);

        return true;
    }

    public Plan load(String planId)
    {
        String sql = "select content from t_life_plan where id = ? and valid is null";
        String res = jdbc.queryForObject(sql, new Object[]{planId}, String.class);

        return LifeinsUtil.toPlan(lifeins, JSON.parseObject(res));
    }
}
