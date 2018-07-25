package lerrain.service.policy;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;

@Service
public class PolicyService {
    @Autowired
    PolicyDao policyDao;

    public Policy getPolicy(Long policyId) {
        if (policyId == null)
            return null;

        return policyDao.loadPolicy(policyId);
    }

    public Long newPolicy(Policy policy) {
        if (policyDao.isExists(policy) != null)
            throw new RuntimeException("policy already exists");

        return policyDao.newPolicy(policy);
    }

    public void surrender(Long policyId, Date surrenderTime) {
        policyDao.surrender(policyId, surrenderTime);
    }

    public Long findPolicy(Long vendorId, String policyNo) {
        return policyDao.findPolicy(vendorId, policyNo);
    }

    public void updatePolicy(Policy policy) {
        policyDao.updatePolicy(policy);
    }

    public Long savePolicyExtra(Long id, Long policyId, Integer type, JSONObject detail, double premium) {
        return policyDao.savePolicyExtra(id, policyId, type, detail, premium);
    }

    public Long savePolicyRecord(Long id, String policyNo, String packageName, Integer status, String partnerName,
                                 Integer type, JSONObject detail, String msg, double premium,String period) {
        return policyDao.savePolicyRecord(id, policyNo, packageName, status, partnerName, type, detail, msg, premium,period);
    }

    public void insertPolicyClause(PolicyClause policyClause, Policy p) {
        policyDao.insertPolicyClause(policyClause, p);
    }

    public void updatePolicyClause(PolicyClause policyClause) {
        policyDao.updatePolicyClause(policyClause);
    }

    public int countPolicyRecord(Integer type,String policyNo,String period) {
        return policyDao.countPolicyRecord(type,policyNo,period);
    }


}
