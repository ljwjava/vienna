package lerrain.service.channel;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
public class ChannelDao
{
    @Autowired
    JdbcTemplate jdbc;

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

        String sql = "select * from t_channel_product_fee where contract_id = ? order by product_id, insure, pay";
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
}
