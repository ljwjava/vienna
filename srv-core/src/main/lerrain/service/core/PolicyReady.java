package lerrain.service.core;

import lerrain.tool.Common;

import java.util.Map;

public class PolicyReady extends Policy
{
    int result;
    String memo;

    public PolicyReady(Map<String, Object> val)
    {
        super(val);

        result = Common.intOf(val.get("result"), -1);
    }

    public boolean compare(Policy policy)
    {
        result = 1;
        memo = null;

        return false;
    }
}
