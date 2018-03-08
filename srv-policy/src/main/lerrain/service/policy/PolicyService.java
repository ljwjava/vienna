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
        return policyDao.insert(policy);
    }

    public void update(Policy policy)
    {
        policyDao.updateBaseInfo(policy);
    }

}
