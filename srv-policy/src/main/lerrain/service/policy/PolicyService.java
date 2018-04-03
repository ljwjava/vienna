package lerrain.service.policy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

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
        if (policyDao.isExists(policy) != null)
            throw new RuntimeException("policy already exists");

        return policyDao.newPolicy(policy);
    }

    public void surrender(Long policyId, Date surrenderTime)
    {
        policyDao.surrender(policyId, surrenderTime);
    }

    public Long findPolicy(Long vendorId, String policyNo)
    {
        return policyDao.findPolicy(vendorId, policyNo);
    }

    public void updatePolicy(Policy policy)
    {
        policyDao.updatePolicy(policy);
    }
}
