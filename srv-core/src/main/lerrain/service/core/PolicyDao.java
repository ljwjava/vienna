package lerrain.service.core;

import lerrain.tool.Common;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
public class PolicyDao
{
    @Autowired
    JdbcTemplate jdbc;

    public List<PolicyReady> loadBatch(String batchUUID)
    {
        List<PolicyReady> r = new ArrayList<>();

        for (Map<String, Object> m : jdbc.queryForList("select * from t_policy_upload where batch_id = ? where result is null", batchUUID))
            r.add(new PolicyReady(m));

        return r;
    }

    public String upload(List<PolicyReady> list)
    {
        String batchUUID = UUID.randomUUID().toString();

        String s1 = "batch_uuid", s2 = "?";
        for (String[] t : Excel.TITLE)
        {
            s1 += ", " + t[1];
            s2 += ", ?";
        }

        List<Object[]> vals = new ArrayList<>();
        for (PolicyReady pr : list)
        {
            Object[] line = new Object[Excel.TITLE.length + 1];
            line[0] = batchUUID;
            for (int i = 0; i < Excel.TITLE.length; i++)
            {
                line[i + 1] = pr.val.get(Excel.TITLE[i][1]);
            }
            vals.add(line);
        }

        jdbc.batchUpdate(String.format("insert into t_policy_upload(%s) values(%s)", s1, s2), vals);

        return batchUUID;
    }

    public Policy find(String policyNo, Long companyId)
    {
        if (Common.isEmpty(policyNo) || companyId == null)
            throw new RuntimeException("policyNo或companyId无法找到");

        Map m = jdbc.queryForMap("select * from t_policy where policyNo = ? and companyId = ? where valid is null", policyNo, companyId);
        return new Policy(m);
    }

    public void setResult(PolicyReady pr)
    {

    }

    public boolean savePolicy(PolicyReady pr)
    {


        return true;
    }
}
