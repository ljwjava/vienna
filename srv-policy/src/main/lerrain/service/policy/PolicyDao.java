package lerrain.service.policy;

import com.alibaba.fastjson.JSON;
import lerrain.tool.Common;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
public class PolicyDao
{
    @Autowired
    JdbcTemplate jdbc;

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
                    p.setEndorse(jdbc.query("select * from t_policy_endorse where policy_id = ? and valid is null order by create_time", new RowMapper<PolicyEndorse>()
                    {

                        @Override
                        public PolicyEndorse mapRow(ResultSet rs, int rowNum) throws SQLException
                        {
                            PolicyEndorse p = (PolicyEndorse)policyOf(new PolicyEndorse(), rs);
                            p.setPolicyId(rs.getLong("policy_id"));
                            p.setEndorseNo(rs.getString("endorse_no"));
                            p.setEndorseTime(rs.getDate("endorse_time"));

                            return p;
                        }
                    }, policyId));

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
        p.setType(rs.getInt("type"));
        p.setTarget(JSON.parseObject(rs.getString("target")));
        p.setDetail(JSON.parseObject(rs.getString("detail")));
        p.setFee(JSON.parseObject(rs.getString("fee")));
        p.setExtra(JSON.parseObject(rs.getString("extra")));
        p.setPremium(rs.getDouble("premium"));
        p.setInsureTime(rs.getDate("insure_time"));
        p.setEffectiveTime(rs.getDate("effective_time"));
        p.setFinishTime(rs.getDate("finish_time"));
        p.setCompanyId(rs.getLong("company_id"));
        p.setAgencyId(rs.getLong("agency_id"));
        p.setOrgId(rs.getLong("org_id"));
        p.setBrokerId(rs.getLong("broker_id"));

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

        p.setPayFreq(Common.intOf(rs.getObject("pay_freq"), 0));
        p.setPayTerm(Common.intOf(rs.getObject("pay_term"), 0));
        p.setPeriod(Common.intOf(rs.getObject("period"), 0));

        return p;
    }
}
