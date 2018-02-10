package lerrain.service.policy;

import java.util.Date;

public class PolicyEndorse extends Policy
{
    Long policyId;

    String endorseNo;

    Date endorseTime;

    public Long getPolicyId()
    {
        return policyId;
    }

    public void setPolicyId(Long policyId)
    {
        this.policyId = policyId;
    }

    public String getEndorseNo()
    {
        return endorseNo;
    }

    public void setEndorseNo(String endorseNo)
    {
        this.endorseNo = endorseNo;
    }

    public Date getEndorseTime()
    {
        return endorseTime;
    }

    public void setEndorseTime(Date endorseTime)
    {
        this.endorseTime = endorseTime;
    }
}
