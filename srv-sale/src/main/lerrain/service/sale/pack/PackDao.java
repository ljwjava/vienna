package lerrain.service.sale.pack;

import com.alibaba.fastjson.JSONObject;
import lerrain.service.common.ServiceMgr;
import lerrain.tool.Common;
import lerrain.tool.formula.Factors;
import lerrain.tool.formula.Formula;
import lerrain.tool.formula.Function;
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
public class PackDao
{
    @Autowired
    JdbcTemplate   jdbc;

    @Autowired
    Lifeins lifeins;

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

    public Map<Object, PackIns> loadPacks()
    {
        final Map<Long, Map> vendorMap = this.loadAllVendors();

        final Map<Object, PackIns> r = new HashMap<>();

        jdbc.query("select * from t_ware_pack", new RowMapper<Object>()
        {
            @Override
            public Object mapRow(ResultSet m, int rowNum) throws SQLException
            {
                try
                {
                    String showStr = m.getString("show");
                    String envStr = m.getString("env");
                    String price = m.getString("price");
                    String referKey = m.getString("refer_key");
                    String formOpt = m.getString("form_opt");

                    Long input = Common.toLong(m.getObject("input"));

                    PackIns packIns = new PackIns();
                    packIns.setId(m.getLong("id"));
                    packIns.setCode(m.getString("code"));
                    packIns.setName(m.getString("name"));
                    packIns.setType(m.getInt("type"));
                    packIns.setApplyMode(m.getInt("apply_mode"));
                    packIns.setReferKey(referKey);

                    if (!Common.isEmpty(envStr))
                        packIns.setEnv(JSONObject.parseObject(envStr));
                    if (!Common.isEmpty(formOpt))
                        packIns.setFormOpt(JSONObject.parseObject(formOpt));
                    if (!Common.isEmpty(showStr))
                        packIns.setShow(JSONObject.parseObject(showStr));
                    if (input != null)
                    {
                        List<InputField> list = loadInputForm(input);
                        packIns.setInputForm(list);
                    }

                    Long vendorId = m.getLong("vendor_id");
                    if (vendorId != null)
                        packIns.setVendor(vendorMap.get(vendorId));

                    if (!Common.isEmpty(price))
                    {
                        if (price.startsWith("factors/"))
                        {
                            String factors = price.substring(8);
                            packIns.setPriceType(PackIns.PRICE_FACTORS);
                            if (!Common.isEmpty(factors))
                                packIns.setPrice(factors.split(","));
                        }
                        else
                        {
                            packIns.setPriceType(PackIns.PRICE_FIXED);
                            packIns.setPrice(new BigDecimal(price));
                        }
                    }

                    packIns.setOpts(loadPackPerform(packIns.getId()));

                    Stack stack = new Stack();
                    stack.declare("PACK", packIns);
                    stack.declare("PACK_ID", packIns.getId());
                    stack.declare("PACK_CODE", packIns.getCode());
                    stack.declare("REFER_KEY", packIns.getReferKey());
                    stack.declare("life", lifeins);
                    stack.setAll(packIns.getEnv());

                    packIns.setStack(stack);

                    r.put(packIns.getId(), packIns);
                    r.put(packIns.getCode(), packIns);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

                return null;
            }
        });

        return r;
    }

    public List<InputField> loadInputForm(Long inputId)
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

                if (Common.isEmpty(c.getVar()))
                    c.setVar(c.getName());

                Formula f = Script.scriptOf(m.getString("detail"));
                if (f != null)
                    c.setDetail(f.run(null));

                String scope = m.getString("scope");
                if (scope != null)
                    c.setScope(scope.split(","));

                return c;
            }
        }, inputId);
    }

    public Map<Long, Map> loadAllVendors()
    {
        final Map<Long, Map> res = new HashMap<>();

        jdbc.query("select id, code, name, logo, succ_tips from t_company", new RowCallbackHandler()
        {
            @Override
            public void processRow(ResultSet rs) throws SQLException
            {
                JSONObject r = new JSONObject();
                r.put("id", rs.getLong("id"));
                r.put("code", rs.getString("code"));
                r.put("name", rs.getString("name"));
                r.put("logo", rs.getString("logo"));
                r.put("succTips", rs.getString("succ_tips"));

                res.put(rs.getLong("id"), r);
            }
        });

        return res;
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
