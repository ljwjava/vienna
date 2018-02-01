package lerrain.service.core;

import com.alibaba.fastjson.JSON;
import lerrain.service.common.Log;
import lerrain.tool.Common;
import lerrain.tool.formula.Factors;
import lerrain.tool.formula.Formula;
import lerrain.tool.formula.Function;
import lerrain.tool.script.Script;
import lerrain.tool.script.Stack;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class PolicyDao
{
    @Autowired
    JdbcTemplate jdbc;

    public List<PolicyReady> loadBatch(Long batchId)
    {
        List<PolicyReady> r = new ArrayList<>();

        for (Map<String, Object> m : jdbc.queryForList("select * from t_policy_upload where batch_id = ? where result is null", batchId))
            r.add(new PolicyReady(m));

        return r;
    }

    public Policy find(String policyNo, Long companyId)
    {
        return null;
    }

    public void setResult(PolicyReady pr)
    {

    }

    public boolean save(PolicyReady pr)
    {
        return true;
    }
}
