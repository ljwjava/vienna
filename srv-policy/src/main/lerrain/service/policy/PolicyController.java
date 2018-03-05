package lerrain.service.policy;

import com.alibaba.fastjson.JSONObject;
import lerrain.service.common.Log;
import lerrain.service.policy.upload.PolicyUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

@Controller
public class PolicyController
{
    @Autowired
    PolicyUploadService policyUploadSrv;

    @Autowired
    PolicyService policySrv;

    @Autowired
    TaskQueue queue;

    @RequestMapping("/upload/excel.json")
    @ResponseBody
    public JSONObject uploadExcel(@RequestBody JSONObject p)
    {
        String idempotent = p.getString("idempotent");
        Long userId = p.getLong("userId");

        queue.add(idempotent, policyUploadSrv.newTask(userId, p.getJSONObject("files").values().toArray()));

        JSONObject res = new JSONObject();
        res.put("result", "success");

        return res;
    }

    @RequestMapping("/view.json")
    @ResponseBody
    public JSONObject view(@RequestBody JSONObject p)
    {
        Long policyId = p.getLong("policyId");

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", policySrv.getPolicy(policyId));

        Log.info(res);

        return res;
    }

    @RequestMapping("/save.json")
    @ResponseBody
    public JSONObject save(@RequestBody JSONObject p)
    {
        Long policyId = p.getLong("policyId");
        Policy policy = policySrv.getPolicy(policyId);

        if (policy == null)
        {
            policy = new Policy();
            policy.setId(policyId);
        }

        policy.setType(p.getIntValue("type"));
        policy.setPlatformId(p.getLong("platformId"));
        policy.setApplyNo(p.getString("applyNo"));
        policy.setPolicyNo(p.getString("policyNo"));

        policy.setTarget(p.getJSONObject("target"));
        policy.setDetail(p.getJSONObject("detail"));
        policy.setFee(p.getJSONObject("fee"));
        policy.setExtra(p.getJSONObject("extra"));

        policy.setPremium(p.getDoubleValue("premium"));

        policy.setInsureTime(p.getDate("insureTime"));
        policy.setEffectiveTime(p.getDate("effectiveTime"));
        policy.setFinishTime(p.getDate("finishTime"));

        policy.setVendorId(p.getLong("vendorId"));
        policy.setAgencyId(p.getLong("agencyId"));
        policy.setOrgId(p.getLong("orgId"));
        policy.setAgentId(p.getLong("agentId"));

        policy.setApplicantName(p.getString("applicantName"));
        policy.setApplicantMobile(p.getString("applicantMobile"));
        policy.setApplicantEmail(p.getString("applicantEmail"));
        policy.setApplicantCertNo(p.getString("applicantCertNo"));
        policy.setApplicantCertType(p.getString("applicantCertType"));
        policy.setInsurantName(p.getString("insurantName"));
        policy.setInsurantCertNo(p.getString("insurantCertNo"));
        policy.setInsurantCertType(p.getString("insurantCertType"));
        policy.setVehicleFrameNo(p.getString("vehicleFrameNo"));
        policy.setVehiclePlateNo(p.getString("vehiclePlateNo"));
        policy.setPayFreq(p.getInteger("payFreq"));
        policy.setPayTerm(p.getInteger("payTerm"));
        policy.setPeriod(p.getInteger("period"));

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", policySrv.savePolicy(policy));

        return res;
    }
}
