package lerrain.service.policy.upload;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lerrain.service.common.Log;
import lerrain.service.policy.Policy;
import lerrain.service.policy.PolicyDao;
import lerrain.tool.Common;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PolicyUploadService
{
    @Autowired
    PolicyUploadDao policyUploadDao;

    @Autowired
    PolicyDao policyDao;

    Map<String, Long> companyMap;

    public String upload(Long userId, Long agencyId, Long orgId, List<Object[]> tab)
    {
        Object[] title = tab.get(0);

        Map<Integer, Integer> map = new HashMap<>();

        for (int j = 0; j < title.length; j++)
        {
            for (int i = 0; i < Excel.TITLE.length; i++)
            {
                String key = Excel.TITLE[i][0];
                if (key.startsWith("*"))
                    key = key.substring(1);

                if (key.equals(Common.trimStringOf(title[j])))
                {
                    map.put(j, i);
                    break;
                }
            }
        }

        for (int i = 0; i < Excel.TITLE.length; i++)
        {
            String key = Excel.TITLE[i][0];
            if (key.startsWith("*"))
            {
                boolean pass = false;
                for (int v : map.values())
                {
                    if (v == i)
                    {
                        pass = true;
                        break;
                    }
                }

                if (!pass)
                    throw new RuntimeException("必须栏目缺失" + key);
            }
        }

        List<String> err = new ArrayList();

        List<PolicyReady> list = new ArrayList<>();
        for (int i = 1; i < tab.size(); i++)
        {
            Object[] row = tab.get(i);

            try
            {
                Map m = new HashMap<>();

                for (int k = 0; k < row.length; k++)
                {
                    if (!map.containsKey(k))
                        continue;

                    int j = map.get(k);

                    if (Excel.TITLE[j].length < 3)
                    {
                        m.put(Excel.TITLE[j][1], t(row[k]));
                    }
                    else
                    {
                        String type = Excel.TITLE[j][2];
                        if ("integer".equals(type))
                            m.put(Excel.TITLE[j][1], Common.toInteger(row[k]));
                        else if ("number".equals(type))
                            m.put(Excel.TITLE[j][1], Common.toDouble(row[k]));
                        else if ("date".equals(type) || "time".equals(type))
                            m.put(Excel.TITLE[j][1], Common.dateOf(row[k]));
                    }
                }

                PolicyReady pr = new PolicyReady(m);
                if (pr.getPolicyNo() == null || pr.getPolicyNo().startsWith("TESTXD00") || (pr.getEndorseNo() != null && pr.getEndorseNo().startsWith("TESTBQ00")))
                    continue;

                pr.put("userId", userId);
                pr.put("agencyId", agencyId);

                String res = pr.verify();
                if (res != null)
                    err.add("上传错误：" + res + " - " + JSON.toJSON(row));
                else
                    list.add(pr);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                err.add("上传错误：" + e.toString() + " - " + JSON.toJSON(row));
            }
        }

        if (!err.isEmpty())
            throw new RuntimeException(JSON.toJSONString(err));

        return policyUploadDao.upload(list, userId);
    }

    private String t(Object v)
    {
        String r = Common.trimStringOf(v);
        if ("".equals(r))
            return null;

        return r;
    }

    public int save(String batchUUID)
    {
        int err = 0;
        int idx = 0;

        List<PolicyReady> list = policyUploadDao.loadBatch(batchUUID);
        for (PolicyReady pr : list)
        {
            idx++;

            try
            {
                pr.prepare(this);

                Policy policy = policyOf(pr);

                if (policyDao.isExists(policy))
                    policyDao.updatePolicy(policy);
                else if (policy.getEndorseNo() == null)
                    policyDao.newPolicy(policy);
                else
                    policyDao.endorsePolicy(policy);

                pr.result = 1;

                System.out.println(idx + " " + pr.result);
            }
            catch (Exception e)
            {
                System.out.println(idx + " " + e.toString());

                err++;

                pr.result = 9;
                pr.memo = e.getMessage();
            }

            policyUploadDao.setResult(pr);
        }

        return err;
    }

    public Long getCompanyId(String companyName)
    {
        if (companyMap == null)
            companyMap = policyUploadDao.loadAllCompany();

        return companyMap.get(companyName);
    }

    private Policy policyOf(PolicyReady pr)
    {
        Policy policy = new Policy();
        policy.setType(2);
        policy.setPlatformId(6L);
        policy.setApplyNo(pr.getString("applyNo"));
        policy.setPolicyNo(pr.getString("policyNo"));
        policy.setEndorseNo(pr.getString("endorseNo"));
        policy.setEndorseTime(pr.getDate("endorseTime"));

        policy.setPremium(pr.getDouble("premium"));

        policy.setInsureTime(pr.getDate("insureTime"));
        policy.setEffectiveTime(pr.getDate("effectiveTime"));
        policy.setFinishTime(pr.getDate("finishTime"));

        policy.setVendorId(pr.getLong("vendorId"));
        policy.setAgencyId(pr.getLong("ownerCompany"));
        policy.setPeriod(1);

        policy.setOwner(pr.getLong("owner"));
        policy.setOwnerOrg(pr.getLong("ownerOrg"));
        policy.setOwnerCompany(pr.getLong("ownerCompany"));

        policy.setApplicantName(pr.getString("applicantName"));
        policy.setApplicantMobile(pr.getString("applicantMobile"));
        policy.setApplicantEmail(pr.getString("applicantEmail"));
        policy.setApplicantCertNo(pr.getString("applicantCertNo"));
        policy.setApplicantCertType(pr.getString("applicantCertType"));
        policy.setInsurantName(pr.getString("insurantName"));
        policy.setInsurantCertNo(pr.getString("insurantCertNo"));
        policy.setInsurantCertType(pr.getString("insurantCertType"));
        policy.setVehicleFrameNo(pr.getString("vehicleFrameNo"));
        policy.setVehiclePlateNo(pr.getString("vehiclePlateNo"));

        JSONObject target = new JSONObject();

        JSONObject app = new JSONObject();
        app.put("name", policy.getApplicantName());
        app.put("mobile", policy.getApplicantMobile());
        app.put("email", policy.getApplicantEmail());
        app.put("certType", policy.getApplicantCertType());
        app.put("certNo", policy.getApplicantCertNo());
        target.put("applicant", app);

        JSONObject ins = new JSONObject();
        ins.put("name", policy.getInsurantName());
        ins.put("certType", policy.getInsurantCertType());
        ins.put("certNo", policy.getInsurantCertNo());
        target.put("insurant", ins);

        JSONObject vehicle = new JSONObject();
        vehicle.put("frameNo", policy.getVehicleFrameNo());
        vehicle.put("plateNo", policy.getVehiclePlateNo());
        target.put("vehicle", vehicle);

        policy.setTarget(target);

        policy.setFee((JSONObject)pr.get("fee"));

        return policy;
    }

    public Long[] findAgent(String agentName, Long companyId, String certNo, String mobile)
    {
        try
        {
            return policyUploadDao.findAgent(agentName, companyId, certNo, mobile);
        }
        catch (Exception e)
        {
            Log.error("找不到代理人：%s, %s, %s", agentName, certNo, mobile);
            return null;
        }
    }

    public Runnable newTask(final Long userId, final Long agencyId, final Long orgId, final Object[] coll)
    {
        return new Runnable()
        {
            @Override
            public void run()
            {
                Excel excel = new Excel();

                for (Object str : coll)
                {
                    List<Object[]> tab = excel.parse((String)str);

                    if (!tab.isEmpty())
                    {
                        String batchUUID = upload(userId, agencyId, orgId, tab);
                        save(batchUUID);
                    }
                }
            }
        };
    }
}
