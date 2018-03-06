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
import java.util.List;

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
        policy.setVehicleFrameNo(p.getString("vehicleFrameNo"));
        policy.setVehiclePlateNo(p.getString("vehiclePlateNo"));

        JSONArray clauses = p.getJSONArray("clauses");
        if (clauses != null)
        {
            policy.setClauses(new ArrayList<PolicyClause>());
            for (int i = 0; i < clauses.size(); i++)
            {
                JSONObject c = clauses.getJSONObject(i);

                PolicyClause pc = new PolicyClause();
                pc.setClauseId(c.getString("clauseId"));
                pc.setClauseCode(c.getString("clauseCode"));
                pc.setClauseName(c.getString("clauseName"));
                pc.setEffectiveTime(c.getDate("effectiveTime"));
                pc.setFinishTime(c.getDate("finishTime"));
                pc.setPremium(c.getDouble("premium"));
                pc.setPayFreq(c.getInteger("payFreq"));
                pc.setPayTerm(c.getInteger("payTerm"));

                policy.getClauses().add(pc);
            }
        }

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", policySrv.savePolicy(policy));

        return res;
    }
}
