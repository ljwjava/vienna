package lerrain.service.policy;

import com.alibaba.fastjson.JSON;
import lerrain.service.common.ServiceTools;
import lerrain.tool.Common;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class PolicyDao
{
    @Autowired
    JdbcTemplate jdbc;

    @Autowired
    ServiceTools tools;

    public Long isExists(Policy p)
    {
        try
        {
            if (p.getEndorseNo() == null)
                return jdbc.queryForObject("select id from t_policy where policy_no = ? and vendor_id = ? and endorse_no is null and valid is null", Long.class, p.getPolicyNo(), p.getVendorId());
            else
                return jdbc.queryForObject("select id from t_policy where policy_no = ? and vendor_id = ? and endorse_no = ? and valid is null", Long.class, p.getPolicyNo(), p.getVendorId(), p.getEndorseNo());
        }
        catch (Exception e)
        {
            return null;
        }
    }

    public Policy loadPolicy(final Long policyId)
    {
        try
        {
            Policy policy = jdbc.queryForObject("select * from t_policy where id = ? and valid is null", new RowMapper<Policy>()
            {
                @Override
                public Policy mapRow(ResultSet rs, int rowNum) throws SQLException
                {
                    Policy p = policyOf(new Policy(), rs);

                    p.setClauses(jdbc.query("select * from t_policy_clause where policy_id = ? and valid is null order by id", new RowMapper<PolicyClause>()
                    {

                        @Override
                        public PolicyClause mapRow(ResultSet rs, int rowNum) throws SQLException
                        {
                            PolicyClause p = new PolicyClause();
                            p.setId(rs.getLong("id"));
                            p.setClauseId(rs.getLong("clause_id"));
                            p.setClauseCode(rs.getString("clause_code"));
                            p.setClauseName(rs.getString("clause_name"));
                            p.setEffectiveTime(rs.getDate("effective_time"));
                            p.setFinishTime(rs.getDate("finish_time"));
                            p.setPay(rs.getString("pay"));
                            p.setInsure(rs.getString("insure"));
                            p.setPurchase(rs.getString("purchase"));
                            p.setPremium(Common.toDouble(rs.getObject("premium")));

                            p.setQuantity(Common.toDouble(rs.getObject("quantity")));
                            p.setAmount(Common.toDouble(rs.getObject("amount")));
                            p.setRank(rs.getString("rank"));

                            return p;
                        }
                    }, policyId));

//                    p.setEndorse(jdbc.query("select * from t_policy_endorse where policy_id = ? and valid is null order by create_time", new RowMapper<PolicyEndorse>()
//                    {
//
//                        @Override
//                        public PolicyEndorse mapRow(ResultSet rs, int rowNum) throws SQLException
//                        {
//                            PolicyEndorse p = (PolicyEndorse)policyOf(new PolicyEndorse(), rs);
//                            p.setPolicyId(rs.getLong("policy_id"));
//                            p.setEndorseNo(rs.getString("endorse_no"));
//                            p.setEndorseTime(rs.getDate("endorse_time"));
//
//                            return p;
//                        }
//                    }, policyId));

                    return p;
                }
            }, policyId);

            return policy;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    private Policy policyOf(Policy p, ResultSet rs) throws SQLException
    {
        p.setId(rs.getLong("id"));
        p.setPlatformId(rs.getLong("platform_id"));
        p.setApplyNo(rs.getString("apply_no"));
        p.setPolicyNo(rs.getString("policy_no"));
        p.setEndorseNo(rs.getString("endorse_no"));
        p.setEndorseTime(rs.getDate("endorse_time"));
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

        p.setVehicleFrameNo(rs.getString("vehicle_frame_no"));
        p.setVehiclePlateNo(rs.getString("vehicle_plate_no"));

        p.setPeriod(Common.intOf(rs.getObject("period"), 0));

        return p;
    }

    public Long newPolicy(Policy p)
    {
        p.setId(tools.nextId("policy"));

        String sql = "insert into t_policy (id, platform_id, apply_no, policy_no, endorse_no, endorse_time, type, target, detail, fee, extra, premium, insure_time, effective_time, finish_time, vendor_id, agency_id, owner_company, owner_org, owner, period, " +
                "applicant_name, applicant_mobile, applicant_email, applicant_cert_no, applicant_cert_type, insurant_name, insurant_cert_no, insurant_cert_type, vehicle_frame_no, vehicle_plate_no, create_time, creator, update_time, updater) " +
                "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, now(), ?, now(), ?)";

        jdbc.update(sql,
                p.getId(),
                p.getPlatformId(),
                p.getApplyNo(),
                p.getPolicyNo(),
                p.getEndorseNo(),
                p.getEndorseTime(),
                p.getType(),
                Common.trimStringOf(p.getTarget()),
                Common.trimStringOf(p.getDetail()),
                Common.trimStringOf(p.getFee()),
                Common.trimStringOf(p.getExtra()),
                p.getPremium(),
                p.getInsureTime(),
                p.getEffectiveTime(),
                p.getFinishTime(),
                p.getVendorId(),
                p.getAgencyId(),
                p.getOwnerCompany(),
                p.getOwnerOrg(),
                p.getOwner(),
                p.getPeriod(),
                p.getApplicantName(),
                p.getApplicantMobile(),
                p.getApplicantEmail(),
                p.getApplicantCertNo(),
                p.getApplicantCertType(),
                p.getInsurantName(),
                p.getInsurantCertNo(),
                p.getInsurantCertType(),
                p.getVehicleFrameNo(),
                p.getVehiclePlateNo(),
                p.getOwner(),
                p.getOwner()
        );

        if (p.getClauses() != null)
        {
            String s1 = "insert into t_policy_clause(id, policy_id, clause_id, clause_code, clause_name, effective_time, finish_time, pay, insure, purchase, amount, quantity, rank, premium, create_time, creator, update_time, updater) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, now(), ?, now(), ?)";
            for (PolicyClause pc : p.getClauses())
                jdbc.update(s1, pc.getId(), p.getId(), pc.getClauseId(), pc.getClauseCode(), pc.getClauseName(), pc.getEffectiveTime(), pc.getFinishTime(), pc.getPay(), pc.getInsure(), pc.getPurchase(), pc.getAmount(), pc.getQuantity(), pc.getRank(), pc.getPremium(), p.getOwner(), p.getOwner());
        }

        return p.getId();
    }

    public void updatePolicy(Policy p)
    {
        if (p.getId() == null)
            throw new RuntimeException("更新保单，缺少policyId");

        String sql = "replace into t_policy (id, platform_id, apply_no, policy_no, endorse_no, endorse_time, type, target, detail, fee, extra, premium, insure_time, effective_time, finish_time, vendor_id, agency_id, owner_company, owner_org, owner, period, " +
                "applicant_name, applicant_mobile, applicant_email, applicant_cert_no, applicant_cert_type, insurant_name, insurant_cert_no, insurant_cert_type, vehicle_frame_no, vehicle_plate_no, update_time, updater) " +
                "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, now(), ?)";

        jdbc.update(sql,
                p.getId(),
                p.getPlatformId(),
                p.getApplyNo(),
                p.getPolicyNo(),
                p.getEndorseNo(),
                p.getEndorseTime(),
                p.getType(),
                Common.trimStringOf(p.getTarget()),
                Common.trimStringOf(p.getDetail()),
                Common.trimStringOf(p.getFee()),
                Common.trimStringOf(p.getExtra()),
                p.getPremium(),
                p.getInsureTime(),
                p.getEffectiveTime(),
                p.getFinishTime(),
                p.getVendorId(),
                p.getAgencyId(),
                p.getOwnerCompany(),
                p.getOwnerOrg(),
                p.getOwner(),
                p.getPeriod(),
                p.getApplicantName(),
                p.getApplicantMobile(),
                p.getApplicantEmail(),
                p.getApplicantCertNo(),
                p.getApplicantCertType(),
                p.getInsurantName(),
                p.getInsurantCertNo(),
                p.getInsurantCertType(),
                p.getVehicleFrameNo(),
                p.getVehiclePlateNo(),
                p.getOwner()
        );
    }
}
