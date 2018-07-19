package lerrain.service.policy.image.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lerrain.service.common.Log;
import lerrain.service.common.ServiceTools;
import lerrain.service.policy.NormalPolicy;
import lerrain.service.policy.Policy;
import lerrain.service.policy.PolicyExtra;
import lerrain.service.policy.common.DateUtils;
import lerrain.service.policy.image.ImageUpload;
import lerrain.tool.Common;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

@Repository
public class ImageDao {

    @Autowired
    JdbcTemplate jdbc;

    @Autowired
    ServiceTools tools;

    public List<ImageUpload> listUploadApply(ImageUpload params) {
        StringBuilder sql = new StringBuilder(
                "select a.id,a.type,a.user_id,a.open_id,a.gmt_created,a.name,a.remark,a.mobile,a.status,a.image_url from t_policy_image_upload a where  a.is_deleted='N' ");

        List<Object> list = addParams(sql, params);
        if (params.getStart() != null && params.getLimit() != null) {
            sql.append(" limit ?,? ");
            list.add(params.getStart());
            list.add(params.getLimit());
        }
        return jdbc.query(sql.toString(), list.toArray(), new RowMapper<ImageUpload>() {
            @Override
            public ImageUpload mapRow(ResultSet rs, int rowNum) throws SQLException {
                ImageUpload apply = new ImageUpload();
                apply.setId(rs.getLong("id"));
                apply.setType(rs.getString("type"));
                apply.setOpenId(rs.getString("open_id"));
                apply.setUserId(rs.getLong("user_id"));
                apply.setGmtCreated(rs.getDate("gmt_created"));
                apply.setName(rs.getString("name"));
                apply.setRemark(rs.getString("remark"));
                apply.setMobile(rs.getString("mobile"));
                apply.setStatus(rs.getString("status"));
                apply.setImageUrl(rs.getString("image_url"));
                return apply;
            }

        });
    }

    private List<Object> addParams(StringBuilder sql, ImageUpload params) {
        List<Object> list = new ArrayList<>();
        if (params.getId() != null) {
            sql.append(" and a.id =? ");
            list.add(params.getId());
        }
        if (StringUtils.isNotBlank(params.getName())) {
            sql.append(" and a.name =? ");
            list.add(params.getName());
        }
        if (StringUtils.isNotBlank(params.getMobile())) {
            sql.append(" and a.mobile =? ");
            list.add(params.getMobile());
        }
        if (params.getUserId() != null) {
            sql.append(" and a.user_id =? ");
            list.add(params.getUserId());
        }
        if (StringUtils.isNotBlank(params.getStartDate())) {
            sql.append(" and a.gmt_created >=? ");
            list.add(params.getStartDate());
        }
        if (StringUtils.isNotBlank(params.getEndDate())) {
            sql.append(" and a.gmt_created <=? ");
            list.add(params.getEndDate());
        }
        if (StringUtils.isNotBlank(params.getStatus())) {
            sql.append(" and a.status =? ");
            list.add(params.getStatus());
        }
        if (params.getPress() != null && params.getPress()) {
            sql.append(" and a.status ='10'  and image_url is null ");
        }
        sql.append(" order by gmt_created desc");
        return list;
    }

    public void deleteUploadApply(Long id) {
        String sql = "update t_policy_image_upload set is_deleted='Y' where id=" + id;
        jdbc.execute(sql);
    }

    public Integer countUploadApply(ImageUpload data) {
        StringBuilder sql = new StringBuilder("select count(*) from t_policy_image_upload a  where a.is_deleted='N' ");
        List<Object> params = addParams(sql, data);
        return jdbc.queryForObject(sql.toString(), params.toArray(), Integer.class);
    }

    public List<Policy> listPolicyByUploadApply(Long imageUploadId) {
        StringBuilder sql = new StringBuilder(
                "select b.* from t_wx_policy a,t_policy b where a.policy_id=b.id and a.is_deleted='N' and a.image_upload_id= ");
        sql.append(imageUploadId);
        return jdbc.query(sql.toString(), new RowMapper<Policy>() {
            @Override
            public Policy mapRow(ResultSet rs, int rowNum) throws SQLException {
                Policy policy = new Policy();
                return policyOf(policy, rs);
            }

        });
    }

    private Policy policyOf(Policy p, ResultSet rs) throws SQLException {
        p.setId(rs.getLong("id"));
        p.setPlatformId(rs.getLong("platform_id"));
        p.setApplyNo(rs.getString("apply_no"));
        p.setPolicyNo(rs.getString("policy_no"));
        p.setEndorseNo(rs.getString("endorse_no"));
        p.setEndorseTime(rs.getDate("endorse_time"));
        p.setProductName(rs.getString("product_name"));
        p.setType(rs.getInt("type"));
        p.setTarget(JSON.parseObject(rs.getString("target")));
        p.setDetail(JSON.parseObject(rs.getString("detail")));
        p.setFee(JSON.parseObject(rs.getString("fee")));
        p.setExtra(JSON.parseObject(rs.getString("extra")));
        p.setPremium(rs.getDouble("premium"));
        p.setInsureTime(rs.getDate("insure_time"));
        p.setEffectiveTime(rs.getDate("effective_time"));
        p.setFinishTime(rs.getDate("finish_time"));
        p.setVendorId(rs.getLong("vendor_id"));
        p.setAgencyId(rs.getLong("agency_id"));
        p.setOwner(rs.getLong("owner"));
        p.setOwnerOrg(rs.getLong("owner_org"));
        p.setOwnerCompany(rs.getLong("owner_company"));

        //
        p.setApplicantName(rs.getString("applicant_name"));
        p.setApplicantMobile(rs.getString("applicant_mobile"));
        p.setApplicantEmail(rs.getString("applicant_email"));
        p.setApplicantCertNo(rs.getString("applicant_cert_no"));
        p.setApplicantCertType(rs.getString("applicant_cert_type"));

        p.setInsurantName(rs.getString("insurant_name"));
        p.setInsurantCertNo(rs.getString("insurant_cert_no"));
        p.setInsurantCertType(rs.getString("insurant_cert_type"));

        p.setVehicleEngineNo(rs.getString("vehicle_engine_no"));
        p.setVehicleFrameNo(rs.getString("vehicle_frame_no"));
        p.setVehiclePlateNo(rs.getString("vehicle_plate_no"));

        p.setPeriod(Common.intOf(rs.getObject("period"), 0));

        return p;
    }

    public void updateImageUpload(ImageUpload data) {
        StringBuilder sql = new StringBuilder("update t_policy_image_upload set ");
        List<Object> params = new ArrayList<>();
        if (data.getGmtModified() != null) {
            sql.append("gmt_modified =? ");
            params.add(data.getGmtModified());
        }
        if (data.getStatus() != null) {
            sql.append(",status=? ");
            params.add(data.getStatus());
        }
        if (data.getType() != null) {
            sql.append(",type=? ");
            params.add(data.getType());
        }
        sql.append(" where id=?").append(data.getId());
        params.add(data.getId());
        jdbc.update(sql.toString(), params.toArray());
    }

    public Integer countRecommend(String name, Long userId, String openId) {
        String sql = "select count(*) from t_policy a LEFT JOIN t_policy_extra b on a.id=b.policy_id where a.insurant_name=? or a.applicant_name=? or b.detail like ? and a.owner=? and not EXISTS (select * from t_wx_policy c where c.is_deleted='N' and a.id=c.policy_id and open_id=?)";
        return jdbc.queryForObject(sql, new Object[] { name, name, name, userId, openId }, Integer.class);
    }

    public List<Map<String, Object>> queryRecommend(String name, Long userId, String openId, Integer start,
                                                    Integer limit) {
        //String sql = "select id,policy_no,product_name,applicant_name,applicant_cert_no,insurant_name,insurant_cert_no from t_policy where insurant_name=? and owner=? union all select id,policy_no,product_name,applicant_name,applicant_cert_no,insurant_name,insurant_cert_no from t_policy where applicant_name=? and owner=? limit ?,?";
        String sql = "select a.id,a.policy_no,a.product_name,a.applicant_name,a.applicant_cert_no,a.insurant_name,a.insurant_cert_no from t_policy a LEFT JOIN t_policy_extra b on a.id=b.policy_id where a.insurant_name=? or a.applicant_name=? or b.detail like ? and a.owner=? and not EXISTS (select * from t_wx_policy c where c.is_deleted='N' and a.id=c.policy_id and c.open_id=?) limit ?,?";
        return jdbc.query(sql, new Object[] { name, name, name, userId, openId, start, limit },
                new RowMapper<Map<String, Object>>() {
                    @Override
                    public Map<String, Object> mapRow(ResultSet rs, int rowNum) throws SQLException {
                        Map<String, Object> map = new HashMap<>();
                        map.put("policyId", rs.getString("id"));
                        map.put("policyNo", rs.getString("policy_no"));
                        map.put("productName", rs.getString("product_name"));
                        map.put("applicantName", rs.getString("applicant_name"));
                        map.put("applicantCertNo", rs.getString("applicant_cert_no"));
                        map.put("insurantName", rs.getString("insurant_name"));
                        map.put("insurantCertNo", rs.getString("insurant_cert_no"));
                        return map;
                    }

                });
    }

    public List<PolicyExtra> queryPolicyExtra(Long policyId) {
        String sql = "select * from t_policy_extra where type=2 and policy_id=" + policyId;
        return jdbc.query(sql, new RowMapper<PolicyExtra>() {
            @Override
            public PolicyExtra mapRow(ResultSet rs, int rowNum) throws SQLException {
                PolicyExtra extra = new PolicyExtra();
                extra.setId(rs.getLong("id"));
                extra.setPolicyId(rs.getLong("policy_id"));
                extra.setType(rs.getInt("type"));
                extra.setDetail(rs.getString("detail"));
                return extra;
            }

        });
    }

    public void insertPolicyExtra(PolicyExtra extra) {
        String sql = "INSERT INTO t_policy_extra (id, policy_id, type, detail, create_time, creator, update_time, updater, valid) VALUES (?, ?, ?, ?, NOW(), 'systme', NOW(), 'system', 'N')";
        jdbc.update(sql, tools.nextId("policyExtra"), extra.getPolicyId(), extra.getType(), extra.getDetail());
    }

    public void updatePolicyExtra(PolicyExtra policyExtra) {
        String sql = "update t_policy_extra set detail=?,update_time=NOW() where id=? ";
        jdbc.update(sql, policyExtra.getDetail(), policyExtra.getId());
    }

    public List<JSONObject> queryWxpolicy(Long policyId, String openId) {
        String sql = "select * from t_wx_policy where policy_id=? and open_id=? and is_deleted='N'";
        return jdbc.query(sql, new Object[] { policyId, openId }, new RowMapper<JSONObject>() {
            @Override
            public JSONObject mapRow(ResultSet rs, int rowNum) throws SQLException {
                JSONObject wxPolicy = new JSONObject();
                wxPolicy.put("id", rs.getLong("id"));
                return wxPolicy;
            }

        });
    }

    public Long insertWxPolicy(String openId, Long imageUploadId, Long policyId) {
        Long nextId = tools.nextId("wxPolicy");
        String sql = "insert into t_wx_policy(id,open_id,policy_id,image_upload_id,is_deleted,creator,gmt_created,modifier,gmt_modified) values(?,?,?,?,'N','system',NOW(),'system',NOW())";
        jdbc.update(sql, nextId, openId, policyId, imageUploadId);
        return nextId;
    }

    public void deletePolicy(Long wxPolicyId, String openId, Long policyId) {
        List<Object> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("update t_wx_policy set is_deleted='Y',gmt_modified=now() ");
        if (wxPolicyId != null) {
            sql.append("where id = ?");
            list.add(wxPolicyId);
        } else {
            sql.append("where open_id = ? and policyId = ?");
            list.add(openId);
            list.add(policyId);
        }
        jdbc.update(sql.toString(), list.toArray());

    }

    public void deletePolicy(Long id, List<Long> saveIds) {
        List<Object> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "update t_wx_policy set is_deleted='Y',gmt_modified=now() where image_upload_id=? ");
        list.add(id);
        if (saveIds != null && !saveIds.isEmpty()) {
            sql.append("and policy_id not in (");
            for (int i = 0; i < saveIds.size(); i++) {
                if (i == 0) {
                    sql.append("?");
                } else {
                    sql.append(",?");
                }
                list.add(saveIds.get(i));
            }
            sql.append(")");
        }

        jdbc.update(sql.toString(), list.toArray());
    }

    public List<NormalPolicy> queryPolicyByOpenId(String openId, Long userId, Integer start, Integer limit) {
        StringBuilder sql = new StringBuilder(
                "select a.id as wxPolicyId,a.policy_id,b.policy_no,b.platform_id,b.insurant_name,b.product_name,b.insure_time,b.applicant_name,b.effective_time,b.finish_time,b.premium from t_wx_policy a,t_policy b where a.open_id=? and b.owner=? and a.policy_id=b.id and a.is_deleted='N' order by a.gmt_modified desc");
        List<Object> params = new ArrayList<>();
        params.add(openId);
        params.add(userId);
        if (start != null && limit != null) {
            sql.append(" limit ?,?");
            params.add(start);
            params.add(limit);
        }
        final Date now = new Date();
        return jdbc.query(sql.toString(), params.toArray(), new RowMapper<NormalPolicy>() {
            @Override
            public NormalPolicy mapRow(ResultSet rs, int rowNum) throws SQLException {
                NormalPolicy wxPolicy = new NormalPolicy();
                wxPolicy.setWxPolicyId(rs.getLong("wxPolicyId"));
                wxPolicy.setId(rs.getLong("policy_id"));
                wxPolicy.setPolicyNo(rs.getString("policy_no"));
                wxPolicy.setPlatformId(rs.getInt("platform_id"));
                wxPolicy.setInsurantName(rs.getString("insurant_name"));
                wxPolicy.setProductName(rs.getString("product_name"));
                wxPolicy.setInsureTime(rs.getString("insure_time"));
                wxPolicy.setApplicantName(rs.getString("applicant_name"));
                wxPolicy.setPremium(rs.getDouble("premium"));
                Date effectiveTime = rs.getDate("effective_time");
                Date finishTime = rs.getDate("finish_time");
                wxPolicy.setEffectiveTime(effectiveTime == null ? null : DateUtils.format(effectiveTime,
                        DateUtils.LONG_DATE_PATTERN));
                wxPolicy.setFinishTime(finishTime == null ? null : DateUtils.format(finishTime,
                        DateUtils.LONG_DATE_PATTERN));
                if (effectiveTime != null && finishTime != null && now.after(effectiveTime) && now.before(finishTime)) {
                    wxPolicy.setIsEffective(true);
                } else {
                    wxPolicy.setIsEffective(false);
                }
                return wxPolicy;
            }

        });
    }

    public void insertUpload(ImageUpload params) {
        params.setId(tools.nextId("policyImgUpload"));
        Log.info("insertUpload params>>>" + JSON.toJSONString(params));
        String sql = "INSERT INTO `t_policy_image_upload` (`id`, `open_id`, `name`, `mobile`, `user_id`, `image_url`, `type`, `status`, `remark`, `is_deleted`, `creator`, `gmt_created`, `modifier`, `gmt_modified`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, 'N', 'system', NOW(), 'system', NOW())";
        jdbc.update(sql, params.getId(), params.getOpenId(), params.getName(), params.getMobile(), params.getUserId(),
                params.getImageUrl(), params.getType(), params.getStatus(), params.getRemark());
    }

}
