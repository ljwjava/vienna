package lerrain.service.lifeins.plan;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lerrain.project.insurance.plan.Commodity;
import lerrain.project.insurance.plan.Plan;
import lerrain.project.insurance.plan.UnstableList;
import lerrain.project.insurance.product.Insurance;
import lerrain.project.insurance.product.InsuranceRecom;
import lerrain.project.insurance.product.Portfolio;
import lerrain.project.insurance.product.Purchase;
import lerrain.service.common.Log;
import lerrain.service.lifeins.Customer;
import lerrain.service.lifeins.LifeinsService;
import lerrain.service.lifeins.LifeinsUtil;
import lerrain.tool.Common;
import lerrain.tool.script.Script;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
public class PlanDao2
{
    @Autowired
    JdbcTemplate jdbc;

    @Autowired
    LifeinsService lifeins;

    public void save(Plan plan)
    {
        Date now = new Date();
        JSONObject json = LifeinsUtil.toSaveJson(plan);

        Customer applicant = (Customer) plan.getApplicant();
        Customer insurant = (Customer) plan.getInsurant();

        if (exists(plan.getId()))
        {
            String sql = "update t_ins_plan set content = ?, APPLICANT = ?, INSURANT = ?, INSURE_TIME = ?, UPDATE_TIME = ? where PLAN_ID = ?";
            jdbc.update(sql, json.toJSONString(), LifeinsUtil.jsonOf(applicant).toJSONString(), LifeinsUtil.jsonOf(insurant).toJSONString(), plan.getInsureTime(), now, plan.getId());
        }
        else
        {
            String sql = "insert into t_ins_plan(PLAN_ID, content, APPLICANT, INSURANT, INSURE_TIME, TYPE, CREATE_TIME, UPDATE_TIME) values(?, ?, ?, ?, ?, ?, ?, ?)";
            jdbc.update(sql, plan.getId(), json.toJSONString(), LifeinsUtil.jsonOf(applicant).toJSONString(), LifeinsUtil.jsonOf(insurant).toJSONString(), plan.getInsureTime(), 1, now, now);
        }

        saveProducts(plan);
    }

    public boolean exists(String planId)
    {
        String sql = "select exists(*) from t_ins_plan where PLAN_ID = ? and VALID is null";
        return jdbc.queryForObject(sql, new Object[]{planId}, Boolean.class);
    }

    public boolean delete(String planId)
    {
        String sql = "update t_ins_plan set VALID = 'N', UPDATE_TIME = ? where PLAN_ID = ?";
        jdbc.update(sql, new Date(), planId);

        return true;
    }

    public Plan load(String planId)
    {
        String sql = "select * from t_ins_plan where id = ? and valid is null";
        return jdbc.queryForObject(sql, new Object[]{planId}, new RowMapper<Plan>()
        {
            @Override
            public Plan mapRow(ResultSet rs, int rowNum) throws SQLException
            {
                String res = rs.getString("content");

                if (!Common.isEmpty(res))
                    return LifeinsUtil.toPlan(lifeins, JSON.parseObject(res));
                else
                    return planOf(rs);
            }
        });
    }

    private Plan planOf(ResultSet m)
    {
        try
        {
            Customer applicant = LifeinsUtil.customerOf(JSONObject.parseObject(m.getString("APPLICANT")));
            Customer insurant = LifeinsUtil.customerOf(JSONObject.parseObject(m.getString("INSURANT")));

            final Plan plan = new Plan(applicant, insurant);
            plan.setId(m.getString("PLAN_ID"));

            final Map<String, Commodity> temp = new HashMap<String, Commodity>();

            jdbc.query("select * from t_ins_product where PLAN_ID = ? order by SEQ", new Object[]{plan.getId()}, new RowMapper<Plan>()
            {
                @Override
                public Plan mapRow(ResultSet m, int arg1) throws SQLException
                {
                    String productId = m.getString("PRODUCT_ID");
                    String seq = m.getString("SEQ");
                    String parentSeq = m.getString("PARENT_SEQ");
                    int auto = m.getInt("AUTO");
                    double quantity = Common.doubleOf(m.getDouble("QUANTITY"), 0);
                    double amount = Common.doubleOf(m.getDouble("AMOUNT"), 0);
                    double premium = Common.doubleOf(m.getDouble("PREMIUM"), 0);

//                            Log.debug(plan.getId() + " - " + parentSeq + " - " + productId + " - " + lifeins.getProduct(productId));

                    Commodity parent = parentSeq == null ? null : temp.get(parentSeq);
                    final Commodity c = new Commodity(plan, parent, (Insurance) lifeins.getProduct(productId), null, null);
                    c.setId(seq);
                    c.setAuto(auto != 0);

                    int input = c.getProduct().getInputMode();
                    if (input == Purchase.AMOUNT || input == Purchase.PREMIUM_AND_AMOUNT || input == Purchase.PREMIUM_OR_AMOUNT)
                    {
                        c.setAmount(amount);
                    }
                    if (input == Purchase.PREMIUM || input == Purchase.PREMIUM_AND_AMOUNT)
                    {
                        c.setPremium(premium);
                    }
                    if (input == Purchase.QUANTITY || input == Purchase.RANK_AND_QUANTITY)
                    {
                        c.setQuantity(quantity);
                    }

                    String value = m.getString("VALUE");
                    if (value != null)
                    {
                        Map<String, Object> map = JSONObject.parseObject(value);
                        for (Map.Entry<String, Object> entry : map.entrySet())
                        {
                            if (entry.getKey().startsWith("OPTION:"))
                                c.setInput(entry.getKey().substring(7), (String) entry.getValue());
                            else
                                c.setValue(entry.getKey(), entry.getValue());
                        }
                    }

                    plan.getCommodityList().addCommodity(parent, c);

                    temp.put(seq, c);

                    return null;
                }
            });

            return plan;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public Map<String, Object> loadAll()
    {
        final Map<String, Object> r = new HashMap<>();

        jdbc.query("select a.* from t_ins_plan a where a.VALID is null", new RowCallbackHandler()
        {
            @Override
            public void processRow(ResultSet rs) throws SQLException
            {
                String planId = rs.getString("PLAN_ID");
                String build = rs.getString("BUILD");

                if (Common.isEmpty(build))
                    r.put(planId, planOf(rs));
                else
                    r.put(planId, Script.scriptOf(build));
            }
        });

        return r;
    }

    private void saveProducts(Plan plan)
    {
        jdbc.update("delete from t_ins_product where PLAN_ID = ?", plan.getId());

        String sql = "insert into t_ins_product(PLAN_ID, SEQ, PRODUCT_ID, PARENT_SEQ, AUTO, PREMIUM, AMOUNT, QUANTITY, VALUE) values(?, ?, ?, ?, ?, ?, ?, ?, ?)";

        for (int i = 0; i < plan.size(); i++)
        {
            Commodity c = plan.getCommodity(i);

            Object[] vv = new Object[9];
            vv[0] = plan.getId();
            vv[1] = c.getId();
            vv[2] = c.getProduct().getId();
            vv[3] = c.getParent() == null ? null : c.getParent().getId();
            vv[4] = c.isAuto() ? 1 : 0;

            int input = c.getProduct().getInputMode();
            if (input == Purchase.PREMIUM || input == Purchase.PREMIUM_AND_AMOUNT)
                vv[5] = c.getPremium();
            if (input == Purchase.AMOUNT || input == Purchase.PREMIUM_AND_AMOUNT)
                vv[6] = c.getAmount();
            if (input == Purchase.QUANTITY || input == Purchase.RANK_AND_QUANTITY)
                vv[7] = c.getQuantity();

            JSONObject value = new JSONObject();

            if (c.getValue() != null)
                value.putAll(c.getValue());

            List<?> typeList = c.getProduct().getOptionType();
            if (typeList != null)
                for (Object type : typeList)
                    value.put("OPTION:" + type, c.getInput((String) type).getCode());

            vv[8] = value.toJSONString();

            jdbc.update(sql, vv);
        }
    }

    public void supplyClauses()
    {
        jdbc.query("select * from t_ins_clause", new RowCallbackHandler()
        {
            @Override
            public void processRow(ResultSet rs) throws SQLException
            {
                String code = rs.getString("code");
                Insurance ins = lifeins.getProduct(code);
                if (ins != null)
                {
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
