package lerrain.service.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PolicyService
{
    @Autowired
    PolicyDao policyDao;

    public void upload()
    {

    }

    public boolean save(Long batchId)
    {
        boolean r = true;

        List<PolicyReady> list = policyDao.loadBatch(batchId);
        for (PolicyReady pr : list)
        {
            try
            {
                String policyNo = pr.getString("policy_no");
                Long companyId = getCompanyId(pr.getString("company"));

                Policy policy = policyDao.find(policyNo, companyId);
                if (pr.compare(policy))
                {
                    if (!policyDao.save(pr))
                        pr.result = 9;
                }

                if (pr.result != 2)
                    r = false;

                policyDao.setResult(pr);
            }
            catch (Exception e)
            {
                r = false;
                e.printStackTrace();
            }
        }

        return r;
    }

    public Long getCompanyId(String name)
    {
        return null;
    }
}
