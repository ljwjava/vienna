package lerrain.service.sale;

import lerrain.tool.Common;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by lerrain on 2017/11/13.
 */
public class GatewayDao
{
    @Autowired
    JdbcTemplate jdbc;

    public Map<Integer, List<Gateway>> loadCommissionDefine()
    {
        final Map<Integer, List<Gateway>> m = new HashMap<>();

        String sql = "select * from s_gateway where valid is null order by seq";

        jdbc.query(sql, new RowCallbackHandler()
        {
            @Override
            public void processRow(ResultSet rs) throws SQLException
            {
                int type = rs.getInt("type");
                String uri = rs.getString("uri");

                List<Gateway> list = m.get(type);
                if (list == null)
                {
                    list = new ArrayList<Gateway>();
                    m.put(type, list);
                }
            }

        });

        return m;
    }
}
