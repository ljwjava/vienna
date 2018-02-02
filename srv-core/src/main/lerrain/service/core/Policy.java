package lerrain.service.core;

import lerrain.tool.Common;

import java.util.Map;

public class Policy
{
    Map<String, Object> val;

    public Policy(Map<String, Object> val)
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
}
