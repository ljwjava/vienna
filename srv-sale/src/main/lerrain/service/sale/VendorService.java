package lerrain.service.sale;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class VendorService
{
    @Autowired
    JdbcTemplate jdbc;

    Map<Long, Map> vendorMap;

    @PostConstruct
    public void initiate()
    {
        vendorMap = new HashMap<>();

        jdbc.query("select id, code, name, logo, succ_tips from t_company", new RowCallbackHandler()
        {
            @Override
            public void processRow(ResultSet rs) throws SQLException
            {
                JSONObject r = new JSONObject();
                r.put("id", rs.getLong("id"));
                r.put("code", rs.getString("code"));
                r.put("name", rs.getString("name"));
                r.put("logo", rs.getString("logo"));
                r.put("succTips", rs.getString("succ_tips"));

                vendorMap.put(rs.getLong("id"), r);
            }
        });
    }

    public Map getVendor(Long vendorId)
    {
        return vendorMap.get(vendorId);
    }
}
