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
        StringBuffer sql = new StringBuffer("select count(*) from t_company");
        if (search != null && !"".equals(search))
            sql.append(" and name like '%" + search + "%' ");

        return jdbc.queryForObject(sql.toString(), Integer.class);
    }

    public List<Channel> list(String search, int from, int number, final Long platformId)
    {
        StringBuffer sql = new StringBuffer("select * from t_company");
        if (search != null && !"".equals(search))
            sql.append(" and name like '%" + search + "%' ");
        sql.append(" order by name");
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
                //p.setUpdateTime(m.getTimestamp("update_time"));

                return p;
            }
        });
    }

    public int countContract(Long companyId)
    {
        return jdbc.queryForObject("select count(*) from t_channel_contract where (party_a = ? or party_b = ?) and valid is null", Integer.class, companyId, companyId);
    }

    public List<ChannelContract> queryContract(Long partyA, Long partyB)
    {
        String sql = "select * from t_channel_contract where (party_a = ? or party_b = ?) and valid is null order by update_time desc";
        return jdbc.query(sql, new Object[] {partyA, partyB}, new RowMapper<ChannelContract>()
        {
            @Override
            public ChannelContract mapRow(ResultSet m, int arg1) throws SQLException
            {
                return contractOf(m, false);
            }
        });
    }

    public List<ChannelContract> listContract(Long companyId, int from, int number)
    {
        String sql = "select * from t_channel_contract where (party_a = ? or party_b = ?) and valid is null order by update_time desc limit ?, ?";
        return jdbc.query(sql, new Object[] {companyId, companyId, from, number}, new RowMapper<ChannelContract>()
        {
            @Override
            public ChannelContract mapRow(ResultSet m, int arg1) throws SQLException
            {
                return contractOf(m, false);
            }
        });
    }

    public ChannelContract loadContract(Long contractId)
    {
        String sql = "select * from t_channel_contract where id = ? and valid is null";
        return jdbc.queryForObject(sql, new Object[] {contractId}, new RowMapper<ChannelContract>()
        {
            @Override
            public ChannelContract mapRow(ResultSet m, int arg1) throws SQLException
            {
                return contractOf(m, true);
            }
        });
    }

    public Long saveContract(ChannelContract cc)
    {
        if (cc.getId() == null)
        {
            cc.setId(tools.nextId("contract"));
            jdbc.update("insert into t_channel_contract(id, platform_id, party_a, party_b, name, begin, end, status, docs, create_time, creator, update_time, updater) values(?,?,?,?,?,?,?,?,?,now(),?,now(),?)",
                    cc.getId(), cc.getPlatformId(), cc.getPartyA(), cc.getPartyB(), cc.getName(), cc.getBegin(), cc.getEnd(), 1, cc.getDocs(), null, null);
        }
        else
        {
            jdbc.update("update t_channel_contract set platform_id=?, party_a=?, party_b=?, name=?, begin=?, end=?, docs=?, update_time=now(), updater=? where id=?",
                    cc.getPlatformId(), cc.getPartyA(), cc.getPartyB(), cc.getName(), cc.getBegin(), cc.getEnd(), cc.getDocs(), null, cc.getId());
        }

        return cc.getId();
    }

    public void updateContract(Long contractId, int status)
    {
        jdbc.update("update t_channel_contract set status=?, update_time=now(), updater=? where id=?",
                status, null, contractId);
    }

    public void deleteContract(Long contractId)
    {
        jdbc.update("update t_channel_contract set valid='N', update_time=now(), updater=? where id=?",
                null, contractId);
    }

    private ChannelContract contractOf(ResultSet m, boolean fee) throws SQLException
    {
        ChannelContract p = new ChannelContract();
        p.setId(m.getLong("id"));
        p.setPlatformId(m.getLong("platform_id"));
        p.setPartyA(m.getLong("party_a"));
        p.setPartyB(m.getLong("party_b"));
        p.setName(m.getString("name"));
        p.setBegin(m.getDate("begin"));
        p.setEnd(m.getDate("end"));
        p.setUpdateTime(m.getTimestamp("update_time"));
        p.setStatus(Common.intOf(m.getObject("status"), 1));

        JSONArray list = JSON.parseArray(m.getString("docs"));
        if (list != null)
        {
            List<Long> docs = new ArrayList<>();
            for (int i = 0; i < list.size(); i++)
                docs.add(list.getLong(i));
            p.setDocs(docs);
        }

        if (fee)
            p.setFeeDefine(loadProductFee(p.getId()));

        return p;
    }

    public List<FeeDefine> loadProductFee(Long contractId)
    {
        String sql = "select a.*, b.* from t_channel_fee_define a, t_channel_contract b where b.valid is null and b.id = ? and a.contract_id = b.id order by product_id, convert(insure, signed), convert(pay, signed), type";
        return jdbc.query(sql, new Object[] {contractId}, new RowMapper<FeeDefine>()
        {
            @Override
            public FeeDefine mapRow(ResultSet m, int arg1) throws SQLException
            {
                return feeDefineOf(Common.toLong(m.getObject("product_id")), m);
            }
        });
    }

    public List<FeeDefine> loadChannelFeeDefine(final Long platformId, Long agencyId, final Long productId, Map rs)
    {
        StringBuffer sql = new StringBuffer("select a.*, b.* from t_channel_fee_define a, t_channel_contract b where b.valid is null and b.status = 2 and a.contract_id = b.id and b.platform_id = ? and (b.party_a = ? or b.party_b = ?) and (a.product_id = ? or a.product_id is null)");

        String pay = Common.trimStringOf(rs.get("pay"));
        String insure = Common.trimStringOf(rs.get("insure"));

        sql.append(" and (pay is null or pay = '" + pay + "')");
        sql.append(" and (insure is null or insure = '" + insure + "')");
        sql.append(" order by sequence");

        return jdbc.query(sql.toString(), new Object[] {platformId, agencyId, agencyId, productId}, new RowMapper<FeeDefine>()
        {
            @Override
            public FeeDefine mapRow(ResultSet m, int arg1) throws SQLException
            {
                return feeDefineOf(productId, m);
            }
        });
    }

    private FeeDefine feeDefineOf(Long productId, ResultSet m) throws SQLException
    {
        FeeDefine p = new FeeDefine();
        p.id = m.getLong("id");
        p.productId = productId;
        p.platformId = m.getLong("platform_id");
        p.contractId = m.getLong("contract_id");
        p.payer = m.getLong("party_a");
        p.drawer = m.getLong("party_b");
        p.begin = m.getDate("begin");
        p.end = m.getDate("end");
        p.pay = m.getString("pay");
        p.insure = m.getString("insure");
        p.type = m.getInt("type");
        p.rate = new BigDecimal[] {
                m.getBigDecimal("f1"),
                m.getBigDecimal("f2"),
                m.getBigDecimal("f3"),
                m.getBigDecimal("f4"),
                m.getBigDecimal("f5")
        };
        p.unit = m.getInt("unit");

        return p;
    }

    public void bill(Long platformId, Long productId, Long payer, Long drawer, int bizType, Long bizId, String bizNo, Long vendorId, double amt, int type, int unit, Date time)
    {
        jdbc.update("insert into t_channel_fee(platform_id, biz_type, biz_id, biz_no, product_id, vendor_id, amount, type, unit, estimate, status, payer, drawer, memo, create_time) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
                platformId, bizType, bizId, bizNo, productId, vendorId, amt, type, unit, time, 0, payer, drawer, null, time);
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
        m.put("productId", rs.getLong("product_id"));
        m.put("amount", rs.getDouble("amount"));
        m.put("estimate", rs.getDate("estimate"));
        m.put("payTime", rs.getDate("pay"));
        m.put("status", rs.getInt("status"));
        m.put("payer", rs.getLong("payer"));
        m.put("drawer", rs.getLong("drawer"));

        return m;
    }

    public void addItem(Long contractId, Long clauseId, Integer unit, Integer pay, Integer insure, int type, Double[] val)
    {
        jdbc.update("insert into t_channel_fee_define(contract_id, product_id, pay, insure, type, f1, f2, f3, f4, f5, unit) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                contractId, clauseId, pay, insure, type, val[0], val[1], val[2], val[3], val[4], unit);
    }

    public void updateItem(Long itemId, Integer unit, int type, Double[] val)
    {
        jdbc.update("update t_channel_fee_define set unit=?, type=?, f1=?, f2=?, f3=?, f4=?, f5=? where id=?",
                unit, type, val[0], val[1], val[2], val[3], val[4], itemId);
    }

    public void removeItem(Long itemId)
    {
        jdbc.update("delete from t_channel_fee_define where id=?", itemId);
    }
}
