package lerrain.service.policy.upload;

import java.util.Map;

public class PolicyEndorse extends PolicyBase
{
    PolicyBase policy;

    public PolicyEndorse(PolicyBase policy, Map<String, Object> val)
    {
        super(val);

        this.policy = policy;
    }
}
