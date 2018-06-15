package lerrain.service.sale;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lerrain.service.common.Log;
import lerrain.tool.Common;
import lerrain.tool.formula.Formula;
import lerrain.tool.script.Script;
import lerrain.tool.script.Stack;
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

@Repository
public class SaleDao
{
    @Autowired
    JdbcTemplate jdbc;

    @Autowired
    Lifeins lifeins;

    public List<Ware> loadAll()
    {
        return jdbc.query("select * from t_ware", new RowMapper<Ware>()
        {
            @Override
            public Ware mapRow(ResultSet m, int rowNum) throws SQLException
            {
                Ware c = new Ware();
                c.setId(m.getLong("id"));
                c.setName(m.getString("name"));
                c.setCode(m.getString("code"));
                c.setAbbrName(m.getString("abbr_name"));
//                c.setType(m.getInt("type"));
//                c.setTag(m.getString("tag"));
                c.setPrice(m.getString("price"));
                c.setLogo(m.getString("logo"));
                c.setRemark(m.getString("remark"));
                c.setSalesFlag(m.getInt("sales_flag"));

                String banner = m.getString("banner");
                if (banner != null) {
                    if(banner.trim().startsWith("[") && banner.trim().endsWith("]")) {
                        c.setBanner(JSONArray.parseArray(banner));
                    } else {
                        c.setBanner(JSONArray.parseArray(JSONArray.toJSONString(banner.split(","))));
                    }
                }

//                String detail = m.getString("detail");
//                if (detail != null)
//                    c.setDetail(JSON.parseArray(detail));

                return c;
            }
        });
    }

    public List<Long> find(Long platformId)
    {
        return jdbc.queryForList("select ware_id from t_platform_ware where platform_id = ?", Long.class, platformId);
    }

    public Double getPackRate(PackIns packIns, String key)
    {
        try
        {
            if (key == null)
                return jdbc.queryForObject("select data from t_ware_pack_rate where pack_id = ?", new Object[]{packIns.getId()}, Double.class);
            else
                return jdbc.queryForObject("select data from t_ware_pack_rate where pack_id = ? and seek = ?", new Object[]{packIns.getId(), key}, Double.class);
        }
        catch (Exception e)
        {
            return null;
        }
    }

    public List<PackIns> loadPacks(final Map<Object, Ware> wareMap, final Map<Long, Map> vendorMap)
    {
        return jdbc.query("select * from t_ware_pack order by sequence desc, id", new RowMapper<PackIns>()
        {
            @Override
            public PackIns mapRow(ResultSet m, int rowNum) throws SQLException
            {
                try
                {
                    String showStr = m.getString("show");
                    String extra = m.getString("extra");
                    String price = m.getString("price");
                    String formOpt = m.getString("form_opt");
                    String opClassify = m.getString("op_classify");

                    Long input = Common.toLong(m.getObject("input"));

                    PackIns packIns = new PackIns();
                    packIns.setId(m.getLong("id"));
                    packIns.setCode(m.getString("code"));
                    packIns.setName(m.getString("name"));
                    packIns.setWare(wareMap.get(m.getLong("ware_id")));
                    packIns.setType(m.getInt("type"));
                    packIns.setApplyMode(m.getInt("apply_mode"));

                    if (!Common.isEmpty(formOpt))
                        packIns.setFormOpt(JSONObject.parseObject(formOpt));
                    if (!Common.isEmpty(showStr))
                        packIns.setShow(JSONObject.parseObject(showStr));
                    if (!Common.isEmpty(extra))
                        packIns.setExtra(JSONObject.parseObject(extra));
                    if (!Common.isEmpty(opClassify))
                        packIns.setOpClassify(JSONObject.parseObject(opClassify));

                    if (input != null)
                    {
                        List<InputField> list = loadInputForm(packIns, input);
                        packIns.setInputForm(list);
                    }

                    Long vendorId = m.getLong("vendor_id");
                    if (vendorId != null)
                        packIns.setVendor(vendorMap.get(vendorId));

                    Stack stack = new Stack();
                    stack.declare("PACK", packIns);
                    stack.declare("PACK_ID", packIns.getId());
                    stack.declare("PACK_CODE", packIns.getCode());
                    stack.declare("life", lifeins);

                    packIns.setStack(stack);

                    if (!Common.isEmpty(price))
                    {
                        if (price.startsWith("factors:"))
                        {
                            String factors = price.substring(8);
                            packIns.setPriceType(PackIns.PRICE_FACTORS);
                            if (!Common.isEmpty(factors))
                                packIns.setPrice(factors.split(","));
                        }
                        else if (price.startsWith("plan:"))
                        {
                            String planId = price.substring(5);
                            packIns.setPriceType(PackIns.PRICE_PLAN);
                            if (!Common.isEmpty(planId))
                            {
                                packIns.setPrice(planId);
                                stack.declare("PLAN_ID", planId);
                            }
                        }
                        else if (price.startsWith("life:"))
                        {
                            String scriptId = price.substring(5);
                            packIns.setPriceType(PackIns.PRICE_LIFE);
                            if (!Common.isEmpty(scriptId))
                            {
                                packIns.setPrice(scriptId);
                            }
                        }
                        else if (price.startsWith("product:"))
                        {
                            String productId = price.substring(8);
                            packIns.setPriceType(PackIns.PRICE_PRODUCT);
                            if (!Common.isEmpty(productId))
                                packIns.setPrice(productId.split(","));
                        }
                        else if (price != null)
                        {
                            packIns.setPriceType(PackIns.PRICE_FIXED);
                            packIns.setPrice(new BigDecimal(price));
                        }
                        else
                        {
                            packIns.setPriceType(PackIns.PRICE_OTHER);
                        }
                    }

                    packIns.setOpts(loadPackPerform(packIns.getId()));

                    return packIns;
                }
                catch (Exception e)
                {
                    Log.error(e);
                    return null;
                }
            }
        });
    }

    public List<InputField> loadInputForm(final PackIns packIns, Long inputId)
    {
        return jdbc.query("select * from t_input where input_id = ? order by seq", new RowMapper<InputField>()
        {
            @Override
            public InputField mapRow(ResultSet m, int rowNum) throws SQLException
            {
                InputField c = new InputField();
                c.setName(m.getString("name"));
                c.setLabel(m.getString("label"));
                c.setType(m.getString("type"));
                c.setWidget(m.getString("widget"));
                c.setVar(m.getString("var"));
                c.setValue(m.getString("value"));

                String scr = m.getString("label_f");
                if (scr != null)
                {
                    Formula f = Script.scriptOf((String) scr);
                    c.setLabel(f);
                }

                if (Common.isEmpty(c.getVar()))
                    c.setVar(c.getName());

                Formula f = Script.scriptOf(m.getString("detail"));
                c.setDetail(f);

                String scope = m.getString("scope");
                if (scope != null)
                    c.setScope(scope.split(","));

                Formula condition = Script.scriptOf(m.getString("condition"));
                c.setCondition(condition);

                if (c.getCondition() != null || scr != null)
                    packIns.setDynamicForm(true);

                return c;
            }
        }, inputId);
    }

    public List<Map> loadAllVendor()
    {
        return jdbc.query("select id, code, name, logo, succ_tips from t_company", new RowMapper<Map>()
        {
            @Override
            public Map mapRow(ResultSet rs, int rowNum) throws SQLException
            {
                JSONObject r = new JSONObject();
                r.put("id", rs.getLong("id"));
                r.put("code", rs.getString("code"));
                r.put("name", rs.getString("name"));
                r.put("logo", rs.getString("logo"));
                r.put("succTips", rs.getString("succ_tips"));

                return r;
            }
        });
    }

    public Map<String, Formula> loadPackPerform(Long packId)
    {
        final Map<String, Formula> res = new HashMap<>();

        jdbc.query("select opt, script from t_ware_pack_perform where pack_id = ?", new RowCallbackHandler()
        {
            @Override
            public void processRow(ResultSet rs) throws SQLException
            {
                res.put(rs.getString("opt"), Script.scriptOf(rs.getString("script")));
            }
        }, packId);

        return res;
    }
}
