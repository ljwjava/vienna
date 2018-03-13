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

    /**
     * 批单时才有
     * @return
     */
    public String getEndorseNo()
    {
        return this.getString("endorse_no");
    }

    public Date getRegisterTime()
    {
        return this.getDate("register_time");
    }

    public String verify()
    {
        String policyNo = getString("policy_no");
        if (Common.isEmpty(policyNo))
            return "保单号为空";

        return null;
    }

    public void prepare(PolicyUploadService pus)
    {
        String[] keys = val.keySet().toArray(new String[0]);
        for (String key : keys)
        {
            Object value = this.get(key);

            int pos = key.indexOf("_");
            while (pos >= 0)
            {
                try
                {
                    key = key.substring(0, pos) + key.substring(pos + 1, pos + 2).toUpperCase() + key.substring(pos + 2);
                }
                catch (Exception e)
                {
                    break;
                }

                pos = key.indexOf("_");
            }

            if (!val.containsKey(key))
                val.put(key, value);
        }

        String policyNo = this.getString("policyNo");
        if (Common.isEmpty(policyNo))
            throw new RuntimeException("保单号为空");
        int pos = policyNo.indexOf("-");
        if (pos >= 0)
            throw new RuntimeException("保单号有误");
        this.put("policyNo", policyNo);

        String companyName = this.getString("companyName");
        Long companyId = pus.getCompanyId(companyName);
        if (companyId == null)
            throw new RuntimeException("公司未找到");
        this.put("vendorId", companyId);

        String operateStr = this.getString("operate");
        if ("新单".equals(operateStr))
            this.put("operate", 1);
        else if ("退保".equals(operateStr))
            this.put("operate", 3);
        else if ("批改".equals(operateStr) || "保全".equals(operateStr))
            this.put("operate", 2);
        else
            throw new RuntimeException("操作类型无法识别");

        String typeStr = this.getString("bizType");
        if ("车险".equals(typeStr))
           this.put("type", 2);
        else
            throw new RuntimeException("保单类型无法识别");

        int mode = Common.intOf(this.get("operate"), 0);
        if (mode == 2)
        {
            if (Common.isEmpty(this.get("endorse_no")))
                throw new RuntimeException("类型为批改，批改单号为空");

        }
        else if (mode == 1)
        {
            pos = policyNo.indexOf("-");
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

        String agentName = this.getString("agentName");
        String agentCertNo = this.getString("agentCertNo");
        String agentMobile = this.getString("agentMobile");
        Long[] agent = pus.findAgent(agentName, this.getLong("agencyId"), agentCertNo, agentMobile);
        if (agent == null)
            throw new RuntimeException("无法关联用户");
        this.put("owner", agent[0]);
        this.put("ownerOrg", agent[1]);
        this.put("ownerCompany", agent[2]);

        double prm = Common.doubleOf(this.get("premium"), 0);
        if (prm > 0)
        {
            if (val.get("income") == null)
            {
                double fr = Common.doubleOf(this.get("income_rate"), 0);
                this.put("income", BigDecimal.valueOf(prm * fr).setScale(2, BigDecimal.ROUND_HALF_UP));
            }

            if (val.get("cms") == null)
            {
                double cr = Common.doubleOf(this.get("cms_rate"), 0);
                this.put("cms", BigDecimal.valueOf(prm * cr).setScale(2, BigDecimal.ROUND_HALF_UP));
            }

            JSONObject fee = new JSONObject();
            fee.put("income", val.get("income"));
            fee.put("incomeRate", val.get("incomeRate"));
            fee.put("cms", val.get("cms"));
            fee.put("cmsRate", val.get("cmsRate"));

            val.put("fee", fee);
        }
        else
        {
            throw new RuntimeException("没有保费");
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
                String endorseNo = this.getEndorseNo();

                List<PolicyEndorse> ls = policy.getEndorse();
                if (ls != null) for (PolicyEndorse p : ls)
                {
                    if (endorseNo.equals(p.getEndorseNo()))
                    {
                        //以登记时间、导入时间判断是否需要覆盖
                        Date d2 = this.getRegisterTime();
                        Date d1 = p.getUpdateTime();

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
                Date d1 = policy.getUpdateTime();

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
