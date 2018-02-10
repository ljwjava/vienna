package lerrain.service.policy.upload;

import java.util.Date;
import java.util.Map;

public class PolicyEndorse extends PolicyBase
{
    PolicyBase policy;

    public PolicyEndorse(PolicyBase policy, Map<String, Object> val)
    {
        super(val);

        this.policy = policy;
    }

    public String PolicyId()
    {
        return this.getString("policy_id");
    }

    public String getEndorseNo()
    {
        return this.getString("endorse_no");
    }

    public Date getEndorseTime()
    {
        return this.getDate("endorse_time");
    }
}
