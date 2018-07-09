package lerrain.service.policy.image.controller;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import lerrain.service.common.Log;
import lerrain.service.policy.NormalPolicy;
import lerrain.service.policy.PolicyClause;
import lerrain.service.policy.PolicyService;
import lerrain.service.policy.common.DateUtils;
import lerrain.service.policy.image.ImageUpload;
import lerrain.service.policy.image.service.ImageService;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

@RestController
@RequestMapping(value = "/img")
public class ImageController {

    @Resource
    private ImageService  imageService;

    @Resource
    private PolicyService policyService;

    //申请类型     申请时间       客户姓名     备注     手机号         状态          影像          操作

    @RequestMapping(value = "/upload_apply_list.json")
    public JSONObject get(@RequestBody JSONObject params) {
        Log.info("params=====" + JSON.toJSONString(params));
        JSONObject res = new JSONObject();
        ImageUpload data = new ImageUpload();
        data.setMobile(params.getString("mobile"));
        data.setName(params.getString("name"));
        if (StringUtils.isNotBlank(params.getString("startDate"))) {
            data.setStartDate(params.getString("startDate") + " 00:00:00");
        }
        if (StringUtils.isNotBlank(params.getString("endDate"))) {
            data.setEndDate(params.getString("endDate") + " 23:59:59");
        }
        Integer total = imageService.countUploadApply(data);
        if (total == 0) {
            JSONObject result = new JSONObject();
            result.put("result", "success");
            result.put("content", res);
            return result;
        }
        Integer start = 0;
        Integer limit = params.getInteger("pageSize") == null ? 10 : params.getInteger("pageSize");
        if (params.getInteger("currentPage") != null) {
            start = (params.getInteger("currentPage") - 1) * limit;
        }
        data.setStart(start);
        data.setLimit(limit);
        List<ImageUpload> listUploadApply = imageService.listUploadApply(data);
        //        UploadApply uploadApply = listUploadApply.get(0);
        //        for (int i = 0; i < 20; i++) {
        //            UploadApply uploadApply2 = new UploadApply();
        //            BeanUtils.copyProperties(uploadApply, uploadApply2);
        //            uploadApply2.setId((long) i);
        //            listUploadApply.add(uploadApply2);
        //        }
        res.put("list", listUploadApply);
        Map<String, Object> pagination = new HashMap<>();
        pagination.put("total", total);
        pagination.put("current", params.getInteger("currentPage") == null ? 1 : params.getInteger("currentPage"));
        res.put("pagination", pagination);
        JSONObject result = new JSONObject();
        result.put("result", "success");
        result.put("content", res);
        return result;
    }

    @RequestMapping(value = "/upload.json")
    public JSONObject upload(@RequestBody ImageUpload params) {
        Log.info("upload_apply params=====" + JSON.toJSONString(params));
        JSONObject res = new JSONObject();
        if (StringUtils.isBlank(params.getOpenId()) || params.getUserId() == null) {
            res.put("result", "false");
            return res;
        }
        if (StringUtils.isBlank(params.getImageUrl())) {
            ImageUpload queryParam = new ImageUpload();
            queryParam.setPress(true);
            queryParam.setOpenId(params.getOpenId());
            queryParam.setUserId(params.getUserId());
            List<ImageUpload> listUploadApply = imageService.listUploadApply(queryParam);
            if (listUploadApply != null && !listUploadApply.isEmpty()) {
                ImageUpload imageUpload = listUploadApply.get(0);
                imageUpload.setType("2");
                imageUpload.setGmtModified(new Date());
                imageService.updateImageUpload(imageUpload);
                res.put("result", "success");
                return res;
            }

        }
        params.setType("1");
        params.setStatus("10");
        imageService.insertUpload(params);
        res.put("result", "success");
        return res;
    }

    @RequestMapping(value = "/upload_apply.json")
    public JSONObject queryUploadApplyById(@RequestBody ImageUpload params) {
        JSONObject res = new JSONObject();
        Long uploadApplyId = params.getId();
        Long policyId = params.getPolicyId();
        JSONObject json = new JSONObject();
        if (uploadApplyId != null) {
            List<ImageUpload> listUploadApply = imageService.listUploadApply(params);
            ImageUpload uploadApply = listUploadApply.get(0);

            json.put("imageUrl", StringUtils.isBlank(uploadApply.getImageUrl()) ? null : uploadApply.getImageUrl()
                    .split(","));
            json.put("id", uploadApply.getId());
            json.put("status", uploadApply.getStatus());
            json.put("name", uploadApply.getName());
            Integer count = imageService.countRecommend(uploadApply.getName(), uploadApply.getUserId());
            json.put("recommend", count);
            List<NormalPolicy> listPolicyByUploadApply = imageService.listPolicyByUploadApply(uploadApply.getId());
            json.put("policy", listPolicyByUploadApply);
        }
        if (policyId != null) {
            NormalPolicy policy = imageService.findPolicyById(policyId);
            List<NormalPolicy> policyList = new ArrayList<>();
            policyList.add(policy);
            json.put("policy", policyList);
        }

        res.put("result", "success");
        res.put("content", json);
        return res;
    }

    private NormalPolicy getMock() {
        NormalPolicy policy = new NormalPolicy();
        policy.setPolicyNo("CLOUD00000000001");
        policy.setApplyNo("810000020040958757");
        policy.setApplicantMobile("18655495105");
        policy.setPeriod(1);
        policy.setEffectiveTime(DateUtils.format(DateUtils.getStartOfDay(new Date()), DateUtils.LONG_DATE_PATTERN));
        policy.setFinishTime(DateUtils.format(DateUtils.getDateAdd(new Date(), Calendar.DATE, 365),
                DateUtils.LONG_DATE_PATTERN));
        policy.setApplicantName("张三");
        policy.setApplicantMobile("18655491001");
        policy.setApplicantEmail("123@qq.com");

        List<Map<String, String>> insurants = new ArrayList<>();
        Map<String, String> map11 = new HashMap<>();
        map11.put("insurantName", "被保人1");
        map11.put("relation", "1");
        Map<String, String> map12 = new HashMap<>();
        map12.put("insurantName", "被保人2");
        map12.put("relation", "2");
        insurants.add(map11);
        insurants.add(map12);
        policy.setInsurants(insurants);

        List<Map<String, String>> beneficiaries = new ArrayList<>();
        Map<String, String> beneficiary1 = new HashMap<>();
        beneficiary1.put("beneficiaryName", "受益人1");
        beneficiary1.put("rate", "0.56");
        Map<String, String> beneficiary12 = new HashMap<>();
        beneficiary12.put("beneficiaryName", "受益人2");
        beneficiary12.put("rate", "0.44");
        beneficiaries.add(beneficiary1);
        beneficiaries.add(beneficiary12);
        policy.setBeneficiaries(beneficiaries);

        policy.setPremium(new Double("723"));
        policy.setProductName("尊享e生");
        policy.setInsurantName("张三");
        policy.setInsurantCertNo("11010119800101007X");
        policy.setVendor("众安");
        policy.setVendorLogoUrl("http://xxxx/xx");
        policy.setPayAccountNo("6013826109005179237");
        policy.setRenewalAccountNo("6013826109005179237");
        policy.setPlatformId(4);

        List<PolicyClause> clauses = new ArrayList<>();
        PolicyClause map1 = new PolicyClause();
        PolicyClause map2 = new PolicyClause();
        map1.setClauseName("条款1");
        map1.setAmount(Double.valueOf("4000"));
        map2.setClauseName("条款2");
        map2.setAmount(Double.valueOf("4000"));
        clauses.add(map1);
        clauses.add(map2);
        policy.setClauses(clauses);

        List<Map<String, String>> customs = new ArrayList<>();
        Map<String, String> map3 = new HashMap<>();
        Map<String, String> map4 = new HashMap<>();
        map3.put("code", "key1");
        map3.put("name", "自定义录入项1");
        map3.put("value", "1");
        map4.put("code", "key2");
        map4.put("name", "自定义录入项2");
        map4.put("value", "2");
        customs.add(map3);
        customs.add(map4);
        policy.setCustoms(customs);

        return policy;
    }

    @RequestMapping(value = "/count_recommend.json")
    public JSONObject countRecommend(@RequestBody JSONObject params) {
        JSONObject res = new JSONObject();
        String name = params.getString("name");
        Long userId = params.getLong("userId");
        if (userId == null || name == null) {
            res.put("result", "success");
            res.put("content", 0);
            return res;
        }
        Integer count = imageService.countRecommend(name, userId);
        res.put("result", "success");
        res.put("content", count);
        return res;
    }

    /**
     * 推荐保单
     * 
     * @param params
     * @return
     */
    @RequestMapping(value = "/recommend.json")
    public JSONObject queryRecommend(@RequestBody JSONObject params) {
        //openId, userId, name
        JSONObject res = new JSONObject();
        String name = params.getString("name");
        Long applyId = params.getLong("id");
        if (applyId == null || name == null) {
            return res;
        }

        Integer start = 0;
        Integer limit = params.getInteger("pageSize") == null ? 10 : params.getInteger("pageSize");
        if (params.getInteger("currentPage") != null) {
            start = (params.getInteger("currentPage") - 1) * limit;
        }
        ImageUpload p = new ImageUpload();
        p.setId(applyId);
        List<ImageUpload> listUploadApply = imageService.listUploadApply(p);
        ImageUpload uploadApply = listUploadApply.get(0);
        List<Map<String, Object>> list = imageService.queryRecommend(name, uploadApply.getUserId(), start, limit);

        Map<String, Object> map = new HashMap<>();
        map.put("result", list);
        map.put("currentPage", params.getInteger("currentPage"));
        map.put("pageSize", limit);
        res.put("result", "success");
        res.put("content", map);
        return res;
    }

    @RequestMapping(value = "/save.json")
    public JSONObject savePolicy(@RequestBody JSONObject params) throws ParseException {
        Log.info("savePolicy params:" + JSON.toJSONString(params));
        JSONObject result = new JSONObject();
        Long imageUploadId = params.getLong("id");
        if (imageUploadId != null) {
            ImageUpload p = new ImageUpload();
            p.setId(params.getLong("id"));
            ImageUpload imageUpload = imageService.listUploadApply(p).get(0);
            if ("20".equals(imageUpload.getStatus())) {
                result.put("result", "false");
                return result;
            }
            String status = params.getString("status");
            JSONArray policyArray = params.getJSONArray("policy");

            if (StringUtils.isBlank(status) || policyArray == null || policyArray.isEmpty()) {
                return result;
            }

            Date now = new Date();
            ImageUpload upload = new ImageUpload();
            upload.setId(imageUploadId);
            upload.setStatus(status);
            upload.setGmtModified(now);
            upload.setOpenId(imageUpload.getOpenId());

            imageService.save(upload, policyArray);
        } else {
            JSONArray policyArray = params.getJSONArray("policy");
            imageService.save(null, policyArray);
        }

        result.put("result", "success");
        return result;

    }

    @RequestMapping(value = "/create_relation.json")
    public JSONObject createRelation(@RequestBody JSONObject params) {
        JSONObject result = new JSONObject();
        Long uploadId = params.getLong("id");
        JSONArray ids = params.getJSONArray("policyId");
        if (uploadId == null || ids == null || ids.isEmpty()) {
            result.put("result", "false");
            return result;
        }
        ImageUpload p = new ImageUpload();
        p.setId(uploadId);
        ImageUpload imageUpload = imageService.listUploadApply(p).get(0);
        imageService.createRelation(imageUpload.getOpenId(), ids);
        result.put("result", "success");
        return result;
    }

    @RequestMapping(value = "/delete_upload_apply.json")
    public JSONObject deleteUploadApply(@RequestBody JSONObject params) {
        JSONObject result = new JSONObject();
        if (params.getLong("id") == null) {
            return result;
        }
        imageService.deleteUploadApply(params.getLong("id"));
        result.put("result", "success");
        return result;
    }

    @RequestMapping(value = "/delete_policy.json")
    public JSONObject deletePolicy(@RequestBody JSONObject params) {
        JSONObject result = new JSONObject();
        if (params.getLong("id") == null) {
            return result;
        }
        imageService.deletePolicy(params.getLong("id"), null, null);
        result.put("result", "success");
        return result;
    }

    @RequestMapping(value = "/queryPolicyByUesr.json")
    public JSONObject queryPolicyByUesr(@RequestBody JSONObject params) {
        String openId = params.getString("openId");
        Long userId = params.getLong("userId");
        JSONObject res = new JSONObject();
        if (openId == null || userId == null) {
            return res;
        }
        List<NormalPolicy> queryPolicyByOpenId = imageService.queryPolicyByOpenId(openId, userId, null, null);
        res.put("result", "success");
        res.put("content", queryPolicyByOpenId);
        return res;
    }

    @RequestMapping(value = "/policy_list.json")
    public JSONObject listPolicy(@RequestBody JSONObject params) {
        JSONObject res = new JSONObject();
        String openId = params.getString("openId");
        Long userId = params.getLong("userId");
        if (openId == null || userId == null) {
            res.put("result", "fasle");
            return res;
        }
        Integer start = 0;
        Integer limit = params.getInteger("pageSize") == null ? 10 : params.getInteger("pageSize");
        if (params.getInteger("currentPage") != null) {
            start = (params.getInteger("currentPage") - 1) * limit;
        }
        Map<String, Object> map = new HashMap<>();
        List<NormalPolicy> queryPolicyByOpenId = imageService.queryPolicyByOpenId(openId, userId, start, limit);
        map.put("policy", queryPolicyByOpenId);
        ImageUpload data = new ImageUpload();
        data.setOpenId(openId);
        data.setUserId(userId);
        data.setStatus("10");
        Integer total = imageService.countUploadApply(data);
        map.put("inHand", total);
        res.put("content", map);
        res.put("result", "success");
        return res;
    }

    @RequestMapping(value = "/policy.json")
    public JSONObject queryPolicy(@RequestBody JSONObject params) {
        JSONObject res = new JSONObject();
        Long policyId = params.getLong("policyId");
        if (policyId == null) {
            res.put("result", "fasle");
            return res;
        }
        NormalPolicy policy = imageService.findPolicyById(policyId);
        res.put("content", policy);
        res.put("result", "success");
        return res;
    }

}
