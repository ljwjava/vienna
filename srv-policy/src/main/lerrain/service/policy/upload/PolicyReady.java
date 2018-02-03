package lerrain.service.policy.upload;

import com.alibaba.fastjson.JSONObject;
import lerrain.tool.Common;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class PolicyReady extends PolicyBase
{
    int result;
    String memo;

    int endorse;

    PolicyBase policy;

    public PolicyReady(Map<String, Object> val)
    {
        super(val);
    }

    public String verify()
    {
        String policyNo = getString("policy_no");
        if (Common.isEmpty(policyNo))
            return "保单号为空";

        return null;
    }

    public void prepare()
    {
        String policyNo = this.getString("policy_no");
        if (Common.isEmpty(policyNo))
            throw new RuntimeException("保单号为空");

        String[] keys = new String[] {"operate", "biz_type", "ins_type", "company_name"};
        for (String key : keys)
        {
            String destKey = Excel.MAPPING.get(key);
            if (destKey == null || "*".equals(destKey))
                destKey = key;

            String s = Common.trimStringOf(this.getString(key));
            JSONObject d = Excel.DICT.get(key);
            boolean find = false;
            for (String str : d.keySet())
            {
                if (("/" + str + "/").indexOf("/" + s + "/") >= 0)
                {
                    find = true;
                    put(destKey, d.getInteger(str));
                    break;
                }
            }

            if (!find)
                throw new RuntimeException(key + "无法匹配");
        }

        int mode = Common.intOf(val.get("operate"), 0);
        if (mode == 2)
        {
            if (Common.isEmpty(val.get("endorse_no")))
                throw new RuntimeException("类型为批改，批改单号为空");

            int pos = policyNo.indexOf("-批改");
            if (pos > 0)
            {
                String p1 = policyNo.substring(0, pos);
                val.put("policy_no", p1);

                endorse = 1;
            }
            else
            {
                throw new RuntimeException("类型为批改，但保单号中未标记批改及批改次数");
            }
        }
        else if (mode == 1)
        {
            int pos = policyNo.indexOf("-");
            if (pos >= 0)
                throw new RuntimeException("类型为新单，但保单号中有特殊标记");
        }
        else if (mode == 3)
        {
            endorse = -1;
        }
        else
        {
            throw new RuntimeException("类型为无法识别");
        }

        double prm = Common.doubleOf(this.get("premium"), 0);
        if (prm > 0)
        {
            if (val.get("fee") == null)
            {
                double fr = Common.doubleOf(this.get("fee_rate"), 0);
                this.put("fee", BigDecimal.valueOf(prm * fr).setScale(2, BigDecimal.ROUND_HALF_UP));
            }

            if (val.get("cms") == null)
            {
                double cr = Common.doubleOf(this.get("cms_rate"), 0);
                this.put("cms", BigDecimal.valueOf(prm * cr).setScale(2, BigDecimal.ROUND_HALF_UP));
            }
        }
    }

    /**
     * 比对有冲突时抛出异常，无需上传返回false
     * @param policy
     * @return
     */
    public boolean compare(PolicyBase policy)
    {
        boolean res = true;

        if (policy != null)
        {
            this.policy = policy;

            if (endorse > 0)
            {
                List<PolicyEndorse> ls = policy.getEndorse();
                if (ls != null) for (PolicyBase p : ls)
                {
                    if (p.getEndorseNo().equals(p.getEndorseNo()))
                    {
                        //以登记时间、导入时间判断是否需要覆盖
                        Date d2 = this.getRegisterTime();
                        Date d1 = p.getRegisterTime();

                        if ((d1 == null && d2 == null) || (d1 == null && d2 != null) || (d1 != null && d2 != null && (d1.equals(d2) || d1.before(d2))))
                        {
                            for (Map.Entry<String, Object> e : val.entrySet())
                            {
                                Object v1 = policy.get(e.getKey());
                                if (v1 != null && e.getValue() == null)
                                    e.setValue(v1);
                            }
                        }
                        else
                        {
                            res = false;
                        }

                        break;
                    }
                }
            }
            else
            {
                //以登记时间、导入时间判断是否需要覆盖
                Date d2 = this.getRegisterTime();
                Date d1 = policy.getRegisterTime();

                if ((d1 == null && d2 == null) || (d1 == null && d2 != null) || (d1 != null && d2 != null && (d1.equals(d2) || d1.before(d2))))
                {
                    for (Map.Entry<String, Object> e : val.entrySet())
                    {
                        Object v1 = policy.get(e.getKey());
                        if (v1 != null && e.getValue() == null)
                            e.setValue(v1);
                    }
                }
                else
                {
                    res = false;
                }
            }
        }

        return res;
    }

    private boolean compare(Object v1, Object v2)
    {
        if (v1 == null && v2 == null)
            return true;
        if (v1 == null && v2 != null)
            return false;
        if (v2 == null && v1 != null)
            return false;

        if (v1.toString().equals(v2.toString()))
            return true;

        return Common.doubleOf(v1, -2) == Common.doubleOf(v2, -1);
    }
}
