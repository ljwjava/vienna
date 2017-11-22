package lerrain.service.lifeins.pack;

import com.alibaba.fastjson.JSONObject;
import lerrain.service.lifeins.plan.PlanDao;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class PackDao
{
    @Autowired
    JdbcTemplate   jdbc;

    @Autowired
    PlanDao planDao;

    public Double getPackRate(PackIns packIns, String key)
    {
        try
        {
            return jdbc.queryForObject("select data from t_ins_pack_rate where pack_id = ? and seek = ?", new Object[]{packIns.getId(), key}, Double.class);
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

        jdbc.query("select * from t_ins_pack", new RowMapper<Object>()
        {
            @Override
            public Object mapRow(ResultSet m, int rowNum) throws SQLException
            {
                try
                {
                    String planId = m.getString("plan_id");
                    String pretreat = m.getString("pretreat");
                    String perform = m.getString("perform");
                    String docs = m.getString("docs");
                    String rateFactors = m.getString("rate_factors");

                    Long input = Common.toLong(m.getObject("input"));

                    PackIns packIns = new PackIns();
                    packIns.setId(m.getLong("id"));
                    packIns.setCode(m.getString("code"));
                    packIns.setPretreat(Script.scriptOf(pretreat));
                    packIns.setPerform(Script.scriptOf(perform));
                    packIns.setPlanId(planId);
                    packIns.setName(m.getString("name"));
                    packIns.setType(m.getInt("type"));

                    if (input != null)
                        packIns.setInputForm(loadInputForm(input));
                    if (!Common.isEmpty(rateFactors))
                        packIns.setRateFactors(rateFactors.split(","));
                    if (docs != null)
                        packIns.setDocs(JSONObject.parseObject(docs));

                    Long vendorId = m.getLong("vendor_id");
                    if (vendorId != null)
                        packIns.setVendor(vendorMap.get(vendorId));

                    packIns.setOpts(loadPackPerform(packIns.getId()));

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

        jdbc.query("select opt, script from t_ins_pack_perform where pack_id = ?", new RowCallbackHandler()
        {
            @Override
            public void processRow(ResultSet rs) throws SQLException
            {
                res.put(rs.getString("opt"), Script.scriptOf(rs.getString("script")));
            }
        }, packId);

        return res;
    }

//    public Formula loadFeeFormula(PackIns packIns, Map<String, Object> vals)
//    {
//        String code = packIns.getCode();
//        String seek = null;
//
//        Map<String, Object> feeFactors = packIns.getFeeFactors();
//        if (feeFactors != null) for (Map.Entry<String, Object> f : feeFactors.entrySet())
//        {
//            Object val = vals.get(f.getKey());
//            if (val == null)
//                val = f.getValue();
//
//            seek = seek == null ? val.toString() : seek + "," + val.toString();
//        }
//
//        String script;
//        if (seek == null)
//            script = jdbc.queryForObject("select fee from t_ins_pack_fee where code = ? and seek is null", new Object[]{code}, String.class);
//        else
//            script = jdbc.queryForObject("select fee from t_ins_pack_fee where code = ? and seek = ?", new Object[]{code, seek}, String.class);
//
//        return Script.scriptOf(script);
//    }
//
//    public Formula loadScript(Long scriptId)
//    {
//        return Script.scriptOf(jdbc.queryForObject("select script from t_script where id = ?", String.class, scriptId));
//    }
//
//    public Map<Integer, double[]> load(Long packId, Long platformId, String payFreq, String payPeriod)
//    {
//        final Map<Integer, double[]> r = new HashMap<>();
//
//        jdbc.query("select * from t_ins_pack_commission where valid is null and pack_id = ? and platform_id = ? and pay_freq = ? and pay_period = ? and begin >= ? and end < ? order by term", new RowCallbackHandler()
//        {
//            @Override
//            public void processRow(ResultSet rs) throws SQLException
//            {
//                int term = rs.getInt("term");
//                double[] rate = new double[2];
//                rate[0] = rs.getDouble("self_rate");
//                rate[1] = rs.getDouble("parent_rate");
//
//                r.put(term, rate);
//            }
//
//        }, packId, platformId, payFreq, payPeriod);
//
//        return r;
//    }
}
