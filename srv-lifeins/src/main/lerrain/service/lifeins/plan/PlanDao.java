package lerrain.service.lifeins.plan;

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

    public void supplyClauses()
    {
        final Map<String, String> map = new HashMap<>();

        jdbc.query("select * from t_company", new RowCallbackHandler()
        {
            @Override
            public void processRow(ResultSet rs) throws SQLException
            {
                map.put(rs.getString("code"), rs.getString("logo"));
            }
        });

        jdbc.query("select * from t_ins_clause", new RowCallbackHandler()
        {
            @Override
            public void processRow(ResultSet rs) throws SQLException
            {
                String code = rs.getString("code");
                Insurance ins = lifeins.getProduct(code);
                if (ins != null)
                {
                    ins.setAdditional("logo", map.get(ins.getCompany().getId()));
                    ins.setAdditional("remark", rs.getString("remark"));
                    ins.setAdditional("tag", rs.getString("tag"));
                }
                else
                {
                    Log.error(code + " is not found.");
                }
            }
        });
    }
}
