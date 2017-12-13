package lerrain.service.sale;

import lerrain.tool.Common;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
public class CmsDao
{
    @Autowired
    JdbcTemplate   jdbc;

    public Map<String, List<CmsDefine>> loadCommissionDefine()
    {
        final Map<String, List<CmsDefine>> m = new HashMap<>();

        String sql = "select * from t_ware_pack_commission where valid is null order by platform_id, pack_id, pay_freq, pay_period, begin, end";

        jdbc.query(sql, new RowCallbackHandler()
        {
            @Override
            public void processRow(ResultSet rs) throws SQLException
            {
                Long platformId = rs.getLong("platform_id");
                Long packId = rs.getLong("pack_id");

                String group = rs.getString("group");
                String payFreq = rs.getString("pay_freq");
                String payPeriod = rs.getString("pay_period");

                String key = platformId + "/" + group + "/" + packId + "/" + payFreq + "/" + payPeriod;

                List<CmsDefine> list = m.get(key);
                if (list == null)
                {
                    list = new ArrayList<CmsDefine>();
                    m.put(key, list);
                }

                double[] self = new double[5];
                double[] parent = new double[5];

                for (int i = 0; i < 5; i++)
                {
                    BigDecimal s = rs.getBigDecimal("self_" + (i + 1));
                    BigDecimal p = rs.getBigDecimal("parent_" + (i + 1));

                    self[i] = s == null ? 0 : s.doubleValue();
                    parent[i] = p == null ? 0 : p.doubleValue();
                }

                int freeze = Common.intOf(rs.getObject("freeze"), 0);
                int unit = Common.intOf(rs.getObject("unit"), 0);

                Date begin = rs.getDate("begin");
                Date end = rs.getDate("end");

                if (unit > 0)
                {
                    CmsDefine pc = new CmsDefine(begin, end, self, parent, freeze, unit);
                    pc.setMemo(rs.getString("memo"));

                    list.add(pc);
                }
            }

        });

        return m;
    }
}
