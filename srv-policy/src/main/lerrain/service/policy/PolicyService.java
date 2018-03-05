package lerrain.service.policy;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lerrain.tool.Common;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public Long savePolicy(Policy policy)
    {
        return policyDao.savePolicy(policy);
    }
}
