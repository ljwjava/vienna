package lerrain.service.policy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PolicyService
{
    @Autowired
    PolicyDao policyDao;

    public Policy getPolicy(Long policyId)
    {
        if (policyId == null)
            return null;

        return policyDao.loadPolicy(policyId);
    }

    public Long newPolicy(Policy policy)
    {
        if (policyDao.isExists(policy))
            throw new RuntimeException("policy already exists");

        return policyDao.newPolicy(policy);
    }

    public void updatePolicy(Policy policy)
    {
        policyDao.updatePolicy(policy);
    }

    public Long endorsePolicy(Policy policy)
    {
        if (policyDao.isExists(policy))
            throw new RuntimeException("policy already exists");

        return policyDao.endorsePolicy(policy);
    }
}
