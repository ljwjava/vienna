package lerrain.service.channel;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lerrain.service.common.ServiceTools;
import lerrain.tool.Common;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
public class ChannelDao
{
    @Autowired JdbcTemplate jdbc;
    @Autowired ServiceTools tools;

    public int count(String search, final Long platformId)
    {
        StringBuffer sql = new StringBuffer("select count(*) from t_company where valid is null");
        if (search != null && !"".equals(search))
            sql.append(" and name like '%" + search + "%' ");

        return jdbc.queryForObject(sql.toString(), Integer.class);
    }

    public List<Channel> list(String search, int from, int number, final Long platformId)
    {
        StringBuffer sql = new StringBuffer("select * from t_company where valid is null");
        if (search != null && !"".equals(search))
            sql.append(" and name like '%" + search + "%' ");
        sql.append(" order by update_time desc");
        sql.append(" limit " + from + ", " + number);

        return jdbc.query(sql.toString(), new RowMapper<Channel>()
        {
            @Override
            public Channel mapRow(ResultSet m, int arg1) throws SQLException
            {
                Channel p = new Channel();
                p.setId(m.getLong("id"));
                p.setName(m.getString("name"));
                p.setType(m.getInt("type"));
                p.setUpdateTime(m.getTimestamp("update_time"));

                return p;
            }
        });
    }

    public List<ChannelContract> loadContract(Long platformId, Long partyA, Long partyB)
    {
        String sql = "select * from t_channel_contract where platform_id = ?, party_a = ? and party_b = ? and valid is null order by update_time desc";
        return jdbc.query(sql, new Object[] {platformId, partyA, partyB}, new RowMapper<ChannelContract>()
        {
            @Override
            public ChannelContract mapRow(ResultSet m, int arg1) throws SQLException
            {
                ChannelContract p = new ChannelContract();
                p.setId(m.getLong("id"));
                p.setName(m.getString("name"));
                p.setBegin(m.getDate("begin"));
                p.setEnd(m.getDate("end"));
                p.setUpdateTime(m.getTimestamp("update_time"));

                JSONArray list = JSON.parseArray(m.getString("docs"));
                if (list != null)
                {
                    List<Long> docs = new ArrayList<>();
                    for (int i = 0; i < list.size(); i++)
                        docs.add(list.getLong(i));
                    p.setDocs(docs);
                }

                return p;
            }
        });
    }

    public List<Map<String, Object>> loadProductFee(Long contractId)
    {
        final Date now = new Date();

        String sql = "select * from t_channel_fee_define where contract_id = ? order by product_id, insure, pay";
        return jdbc.queryForList(sql, new Object[] {contractId}, new RowMapper<Map>()
        {
            @Override
            public Map mapRow(ResultSet m, int arg1) throws SQLException
            {
                Date d1 = m.getDate("begin");
                Date d2 = m.getDate("end");

                JSONObject p = new JSONObject();
                p.put("productId", m.getLong("product_id"));
                p.put("pay", m.getString("pay"));
                p.put("insure", m.getString("insure"));
                p.put("begin", d1);
                p.put("end", d2);
                p.put("f1", m.getBigDecimal("f1"));
                p.put("f2", m.getBigDecimal("f2"));
                p.put("f3", m.getBigDecimal("f3"));
                p.put("f4", m.getBigDecimal("f4"));
                p.put("f5", m.getBigDecimal("f5"));
                p.put("unit", m.getInt("unit"));

                boolean valid = true;
                if (d1 != null && d1.after(now))
                    valid = false;
                if (d2 != null && d2.before(now))
                    valid = false;
                p.put("valid", valid);

                return p;
            }
        });
    }

    public List<FeeDefine> loadChannelFeeDefine(final Long platformId, Long agencyId, final String productId, Map rs)
    {
        StringBuffer sql = new StringBuffer("select a.*, b.* from t_channel_fee_define a, t_channel_contract b where a.contract_id = b.id and b.platform_id = ? and (b.party_a = ? or b.party_b = ?) and (a.product_id = ? or a.product_id is null)");

        String pay = Common.trimStringOf(rs.get("pay"));
        String insure = Common.trimStringOf(rs.get("insure"));

        if (pay != null)
            sql.append(" and (pay is null or pay = '" + pay + "')");
        if (insure != null)
            sql.append(" and (insure is null or insure = '" + insure + "')");

        sql.append(" order by sequence desc");

        return jdbc.query(sql.toString(), new Object[] {platformId, agencyId, agencyId, productId}, new RowMapper<FeeDefine>()
        {
            @Override
            public FeeDefine mapRow(ResultSet m, int arg1) throws SQLException
            {
                FeeDefine p = new FeeDefine();
                p.platformId = platformId;
                p.productId = productId;
                p.payer = m.getLong("party_a");
                p.drawer = m.getLong("party_b");
                p.begin = m.getDate("begin");
                p.end = m.getDate("end");
                p.feeRate = new BigDecimal[] {
                    m.getBigDecimal("f1"),
                    m.getBigDecimal("f2"),
                    m.getBigDecimal("f3"),
                    m.getBigDecimal("f4"),
                    m.getBigDecimal("f5")
                };
                p.unit = m.getInt("unit");

                return p;
            }
        });
    }

//    public Long getVendor(String productId)
//    {
//        try
//        {
//            return jdbc.queryForObject("select company_id from t_product where id = ?", Long.class, productId);
//        }
//        catch (Exception e)
//        {
//            return null;
//        }
//    }

    public void bill(FeeDefine c, Integer bizType, Long bizId, String bizNo, Long vendorId, double amt, int unit, Date time)
    {
        jdbc.update("insert into t_channel_fee(platform_id, biz_type, biz_id, biz_no, product_id, vendor_id, amount, unit, estimate, status, payer, drawer, memo, create_time) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
                c.getPlatformId(), bizType, bizId, bizNo, c.getProductId(), vendorId, amt, unit, time, 0, c.getPayer(), c.getDrawer(), null, time);
    }

    public List findBill(Long platformId, Long vendorId, String bizNo)
    {
        return jdbc.query("select * from t_channel_fee where platform_id = ? and vendor_id = ? and biz_no = ?", new RowMapper()
        {
            @Override
            public Object mapRow(ResultSet rs, int j) throws SQLException
            {
                return billOf(rs);
            }

        }, platformId, vendorId, bizNo);
    }

    public List findBill(int bizType, Long bizId)
    {
        return jdbc.query("select * from t_channel_fee where biz_type = ? and biz_id = ?", new RowMapper()
        {
            @Override
            public Object mapRow(ResultSet rs, int j) throws SQLException
            {
                return billOf(rs);
            }

        }, bizType, bizId);
    }

    private Map billOf(ResultSet rs) throws SQLException
    {
        Map m = new HashMap();
        m.put("amount", rs.getDouble("amount"));
        m.put("estimate", rs.getDate("estimate"));
        m.put("payTime", rs.getDate("pay"));
        m.put("status", rs.getInt("status"));
        m.put("payer", rs.getLong("payer"));
        m.put("drawer", rs.getLong("drawer"));

        return m;
    }
}
