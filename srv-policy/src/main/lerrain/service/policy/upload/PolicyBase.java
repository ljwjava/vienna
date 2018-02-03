package lerrain.service.policy.upload;

import lerrain.tool.Common;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class PolicyBase
{
    Map<String, Object> val;

    List<PolicyEndorse> endorse;

    public PolicyBase(Map<String, Object> val)
    {
        this.val = val;
    }

    public Object get(String key)
    {
        return val.get(key);
    }

    public String getString(String key)
    {
        return Common.trimStringOf(get(key));
    }

    public Long getLong(String key)
    {
        return Common.toLong(get(key));
    }

    public void put(String k, Object v)
    {
        val.put(k, v);
    }

    public Double getDouble(String key)
    {
        return Common.toDouble(get(key));
    }

    public List<PolicyEndorse> getEndorse()
    {
        return endorse;
    }

    public void setEndorse(List<PolicyEndorse> endorse)
    {
        this.endorse = endorse;
    }

    public String getPolicyNo()
    {
        return this.getString("policy_no");
    }

    public Long getCompanyId()
    {
        return this.getLong("company_id");
    }

    public Long getId()
    {
        return this.getLong("id");
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
        return Common.dateOf(this.get("register_time"));
    }
}
