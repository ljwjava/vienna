package lerrain.service.policy.upload;

import lerrain.tool.Common;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class PolicyBase
{
    Map<String, Object> val;

    List<PolicyEndorse> list;

    public PolicyBase(Map<String, Object> val)
    {
        this.val = val;
    }

    public void setEndorse(List<Map<String, Object>> el)
    {
        if (list == null || list.isEmpty())
            return;

        for (Map<String, Object> val : el)
            list.add(new PolicyEndorse(this, val));
    }

    public List<PolicyEndorse> getEndorse()
    {
        return list;
    }

    public Date getDate(String key)
    {
        return Common.dateOf(get(key));
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

    public Date getUpdateTime()
    {
        return this.getDate("update_time");
    }
}
