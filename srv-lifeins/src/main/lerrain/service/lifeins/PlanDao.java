package lerrain.service.lifeins;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lerrain.project.insurance.plan.Plan;
import lerrain.project.insurance.product.Insurance;
import lerrain.service.common.Log;
import lerrain.service.lifeins.LifeinsService;
import lerrain.service.lifeins.LifeinsUtil;
import lerrain.tool.Common;
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
        JSONObject json = LifeinsUtil.toSaveJson(plan);

        String sql = "replace into t_ins_plan(id, plan, update_time) values(?, ?, ?)";
        jdbc.update(sql, plan.getId(), json.toJSONString(), now);
    }

    public Plan load(String planId)
    {
        String sql = "select plan from t_ins_plan where id = ? and valid is null";
        String res = jdbc.queryForObject(sql, new Object[]{planId}, String.class);

        return LifeinsUtil.toPlan(lifeins, JSON.parseObject(res));
    }
}
