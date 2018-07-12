package lerrain.service.policy.image.service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import lerrain.service.policy.NormalPolicy;
import lerrain.service.policy.Policy;
import lerrain.service.policy.PolicyClause;
import lerrain.service.policy.PolicyExtra;
import lerrain.service.policy.PolicyService;
import lerrain.service.policy.common.DateUtils;
import lerrain.service.policy.image.ImageUpload;
import lerrain.service.policy.image.dao.ImageDao;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

@Service
public class ImageService {
    @Resource
    private ImageDao      imageDao;

    @Resource
    private PolicyService policyService;

    public List<ImageUpload> listUploadApply(ImageUpload params) {
        return imageDao.listUploadApply(params);
    }

    public void deleteUploadApply(Long id) {
        imageDao.deleteUploadApply(id);

    }

    public void updateImageUpload(ImageUpload data) {
        imageDao.updateImageUpload(data);
    };

    public Integer countUploadApply(ImageUpload data) {
        return imageDao.countUploadApply(data);
    }

    public List<NormalPolicy> listPolicyByUploadApply(Long imageUploadId) {
        List<Policy> listPolicyByUploadApply = imageDao.listPolicyByUploadApply(imageUploadId);
        if (listPolicyByUploadApply == null || listPolicyByUploadApply.isEmpty()) {
            return new ArrayList<>();
        }
        List<NormalPolicy> resultList = new ArrayList<>();
        for (Policy policy : listPolicyByUploadApply) {
            resultList.add(convert2NormalPolicy(policy));
        }
        return resultList;
    }

    private NormalPolicy convert2NormalPolicy(Policy policy) {
        NormalPolicy np = new NormalPolicy();
        BeanUtils.copyProperties(policy, np);
        Date effectiveTime = policy.getEffectiveTime();
        Date finishTime = policy.getFinishTime();
        np.setEffectiveTime(effectiveTime == null ? null : DateUtils.format(effectiveTime, DateUtils.LONG_DATE_PATTERN));
        np.setFinishTime(finishTime == null ? null : DateUtils.format(finishTime, DateUtils.LONG_DATE_PATTERN));
        //        if (effectiveTime != null && finishTime != null && now.after(effectiveTime) && now.before(finishTime)) {
        //            np.setIsEffective(true);
        //        } else {
        //            np.setIsEffective(false);
        //        }
        List<PolicyExtra> queryPolicyExtra = imageDao.queryPolicyExtra(policy.getId());
        if (queryPolicyExtra == null || queryPolicyExtra.isEmpty()) {
            return np;
        }
        PolicyExtra extra = queryPolicyExtra.get(0);
        if (StringUtils.isBlank(extra.getDetail())) {
            return np;
        }
        JSONObject json = JSONObject.parseObject(extra.getDetail());
        np.setPayAccountNo(json.getString("payAccountNo"));
        np.setRenewalAccountNo(json.getString("renewalAccountNo"));
        np.setRelation(json.getString("relation"));
        np.setVendor(json.getString("vendor"));
        np.setVendorPhone(json.getString("vendorPhone"));
        np.setVendorLogoUrl(json.getString("vendorLogoUrl"));
        if (StringUtils.isBlank(np.getInsurantName())) {
            np.setInsurants(getList(json.getJSONArray("insurants")));
        }
        np.setBeneficiaries(getList(json.getJSONArray("beneficiaries")));
        np.setCustoms(getList(json.getJSONArray("customs")));
        return np;
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, String>> getList(JSONArray array) {
        if (array == null || array.isEmpty()) {
            return new ArrayList<>();
        }
        List<Map<String, String>> list = new ArrayList<>();
        for (int i = 0; i < array.size(); i++) {
            list.add(array.getObject(0, Map.class));
        }
        return list;
    }

    public Integer countRecommend(String name, Long userId) {
        return imageDao.countRecommend(name, userId);
    }

    public List<Map<String, Object>> queryRecommend(String name, Long userId, Integer start, Integer limit) {
        return imageDao.queryRecommend(name, userId, start, limit);
    }

    public void save(ImageUpload upload, JSONArray policyArray) throws ParseException {
        if (upload != null) {
            updateImageUpload(upload);
        }

        for (int i = 0; i < policyArray.size(); i++) {
            NormalPolicy normalPolicy = policyArray.getObject(i, NormalPolicy.class);
            String isDeleted = normalPolicy.getIsDeleted();
            if ("Y".equals(isDeleted)) {
                if (normalPolicy.getId() != null) {
                    imageDao.deletePolicy(null, upload.getOpenId(), normalPolicy.getId());
                }
                continue;
            }

            Policy policy = convert2Policy(normalPolicy);

            JSONObject extra = new JSONObject();
            extra.put("payAccountNo", normalPolicy.getPayAccountNo());
            extra.put("renewalAccountNo", normalPolicy.getRenewalAccountNo());
            extra.put("vendor", normalPolicy.getVendor());
            extra.put("vendorPhone", normalPolicy.getVendorPhone());
            extra.put("vendorLogoUrl", normalPolicy.getVendorLogoUrl());
            extra.put("insurants", removeEmpty(normalPolicy.getInsurants()));
            extra.put("beneficiaries", removeEmpty(normalPolicy.getBeneficiaries()));
            extra.put("customs", removeEmpty(normalPolicy.getCustoms()));
            extra.put("relation", normalPolicy.getRelation());
            if (policy.getId() == null) {
                policyService.newPolicy(policy);
                PolicyExtra policyExtra = new PolicyExtra();
                policyExtra.setPolicyId(policy.getId());
                policyExtra.setDetail(extra.toJSONString());
                policyExtra.setType(2);
                imageDao.insertPolicyExtra(policyExtra);
            } else {
                policyService.updatePolicy(policy);
                if (policy.getClauses() != null && !policy.getClauses().isEmpty()) {
                    saveOrUpdateClause(policy.getClauses(), policy);
                }
                List<PolicyExtra> queryPolicyExtra = imageDao.queryPolicyExtra(policy.getId());
                if (queryPolicyExtra == null || queryPolicyExtra.isEmpty()) {
                    PolicyExtra policyExtra = new PolicyExtra();
                    policyExtra.setDetail(extra.toJSONString());
                    policyExtra.setPolicyId(policy.getId());
                    policyExtra.setType(2);
                    imageDao.insertPolicyExtra(policyExtra);
                } else {
                    PolicyExtra policyExtra = queryPolicyExtra.get(0);
                    policyExtra.setDetail(extra.toJSONString());
                    imageDao.updatePolicyExtra(policyExtra);
                }
            }

        }

    }

    private void saveOrUpdateClause(List<PolicyClause> list, Policy policy) {
        for (PolicyClause policyClause : list) {
            if (policyClause.getId() == null) {
                policyService.insertPolicyClause(policyClause, policy);
            } else {
                policyService.updatePolicyClause(policyClause);
            }
        }
    }

    private List<Map<String, String>> removeEmpty(List<Map<String, String>> list) {
        if (list == null || list.isEmpty()) {
            return list;
        }
        Iterator<Map<String, String>> iterator = list.iterator();
        while (iterator.hasNext()) {
            Map<String, String> next = iterator.next();
            Collection<String> values = next.values();
            boolean remove = true;
            for (String value : values) {
                if (StringUtils.isNotBlank(value) && !"null".equals(value)) {
                    remove = false;
                    break;
                }
            }
            if (remove) {
                iterator.remove();
            }

        }
        return list;
    }

    private Policy convert2Policy(NormalPolicy normalPolicy) throws ParseException {
        Policy policy = new Policy();
        BeanUtils.copyProperties(normalPolicy, policy);
        if (StringUtils.isNotBlank(normalPolicy.getEffectiveTime())) {
            policy.setEffectiveTime(DateUtils.string2Date(normalPolicy.getEffectiveTime() + " 00:00:00",
                    DateUtils.LONG_DATE_PATTERN));
        }
        if (StringUtils.isNotBlank(normalPolicy.getFinishTime())) {
            policy.setFinishTime(DateUtils.string2Date(normalPolicy.getFinishTime() + " 23:59:59",
                    DateUtils.LONG_DATE_PATTERN));
        }
        return policy;
    }

    public void createRelation(String openId, JSONArray ids) {
        for (int i = 0; i < ids.size(); i++) {
            List<JSONObject> queryWxpolicy = imageDao.queryWxpolicy(ids.getLong(i), openId);
            if (queryWxpolicy != null && !queryWxpolicy.isEmpty()) {
                continue;
            }
            imageDao.insertWxPolicy(openId, ids.getLong(i));
        }

    }

    public void deletePolicy(Long id, String openId, Long policyId) {
        imageDao.deletePolicy(id, openId, policyId);

    }

    public List<NormalPolicy> queryPolicyByOpenId(String openId, Long userId, Integer start, Integer limit) {
        return imageDao.queryPolicyByOpenId(openId, userId, start, limit);
    }

    public NormalPolicy findPolicyById(Long policyId) {
        Policy policy = policyService.getPolicy(policyId);
        return convert2NormalPolicy(policy);
    }

    public void insertUpload(ImageUpload params) {
        imageDao.insertUpload(params);

    }

}
