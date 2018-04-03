package lerrain.service.policy;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lerrain.service.common.Log;
import lerrain.service.policy.upload.PolicyUploadService;
import lerrain.tool.Common;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
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
        Log.info(p);

        String idempotent = p.getString("idempotent");
        Long userId = p.getLong("userId");
        Long orgId = p.getLong("orgId");
        Long agencyId = p.getLong("agencyId");

        queue.add(idempotent, policyUploadSrv.newTask(userId, agencyId, orgId, p.getJSONObject("files").values().toArray()));

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

        return res;
    }

    @RequestMapping("/find.json")
    @ResponseBody
    public JSONObject find(@RequestBody JSONObject p)
    {
        String policyNo = p.getString("policyNo");
        Long vendorId = p.getLong("vendorId");

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", policySrv.findPolicy(vendorId, policyNo));

        return res;
    }

    @RequestMapping("/update.json")
    @ResponseBody
    public JSONObject update(@RequestBody JSONObject p)
    {
        Long policyId = p.getLong("policyId");
        Policy policy = policySrv.getPolicy(policyId);
        policy = policyOf(policy, p);

        policySrv.updatePolicy(policy);

        JSONObject res = new JSONObject();
        res.put("result", "success");

        return res;
    }

    @RequestMapping("/surrender.json")
    @ResponseBody
    public JSONObject status(@RequestBody JSONObject p)
    {
        Long policyId = p.getLong("policyId");
        Date surrenderTime = p.getDate("surrenderTime");

        policySrv.surrender(policyId, surrenderTime);

        JSONObject res = new JSONObject();
        res.put("result", "success");

        return res;
    }

    @RequestMapping("/new.json")
    @ResponseBody
    public JSONObject newPolicy(@RequestBody JSONObject p)
    {
        Log.info(p);

        Policy policy = policyOf(new Policy(), p);

        if (Common.isEmpty(policy.getPolicyNo()) || policy.getVendorId() == null)
            throw new RuntimeException("policy no is null or vendor is null");

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", policySrv.newPolicy(policy));

        return res;
    }

    private Policy policyOf(Policy policy, JSONObject p)
    {
        policy.setType(p.getIntValue("type"));
        policy.setPlatformId(p.getLong("platformId"));
        policy.setApplyNo(p.getString("applyNo"));
        policy.setPolicyNo(p.getString("policyNo"));
        policy.setEndorseNo(p.getString("endorseNo"));
        policy.setEndorseTime(p.getDate("endorseTime"));
        policy.setStatus(Common.intOf(p.get("status"), 1));

        policy.setProductName(p.getString("productName"));
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
        policy.setPeriod(Common.intOf(p.get("period"), 0));

        policy.setOwner(p.getLong("owner"));
        policy.setOwnerOrg(p.getLong("ownerOrg"));
        policy.setOwnerCompany(p.getLong("ownerCompany"));

        policy.setApplicantName(p.getString("applicantName"));
        policy.setApplicantMobile(p.getString("applicantMobile"));
        policy.setApplicantEmail(p.getString("applicantEmail"));
        policy.setApplicantCertNo(p.getString("applicantCertNo"));
        policy.setApplicantCertType(p.getString("applicantCertType"));
        policy.setInsurantName(p.getString("insurantName"));
        policy.setInsurantCertNo(p.getString("insurantCertNo"));
        policy.setInsurantCertType(p.getString("insurantCertType"));
        policy.setVehicleEngineNo(p.getString("vehicleEngineNo"));
        policy.setVehicleFrameNo(p.getString("vehicleFrameNo"));
        policy.setVehiclePlateNo(p.getString("vehiclePlateNo"));

        JSONObject detail = p.getJSONObject("detail");
        JSONArray clauses = detail == null ? null : detail.getJSONArray("clauses");
        if (clauses != null)
        {
            policy.setClauses(new ArrayList<PolicyClause>());
            for (int i = 0; i < clauses.size(); i++)
            {
                JSONObject c = clauses.getJSONObject(i);

                PolicyClause pc = new PolicyClause();
                pc.setClauseId(c.getLong("clauseId"));
                pc.setClauseCode(c.getString("clauseCode"));
                pc.setClauseName(c.getString("clauseName"));
                pc.setEffectiveTime(c.getDate("effectiveTime"));
                pc.setFinishTime(c.getDate("finishTime"));
                pc.setPremium(c.getDouble("premium"));
                pc.setPay(c.getString("pay"));
                pc.setInsure(c.getString("insure"));
                pc.setPurchase(c.getString("purchase"));
                pc.setQuantity(c.getDouble("quantity"));
                pc.setAmount(c.getDouble("amount"));
                pc.setRank(c.getString("rank"));

                policy.getClauses().add(pc);
            }
        }

        return policy;
    }
}
