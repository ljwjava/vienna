package lerrain.service.product.fee;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lerrain.service.common.Log;
import lerrain.service.common.ServiceTools;
import lerrain.tool.Common;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
public class FeeDao
{
	@Autowired JdbcTemplate jdbc;
	@Autowired ServiceTools tools;

	public Object valOf(String str)
	{
		if (str == null)
			return null;

		str = str.trim();
		if (str.startsWith("["))
			return JSON.parseArray(str);
		if (str.startsWith("{"))
			return JSON.parseObject(str);
		if (str.indexOf(",") >= 0)
		{
			String[] ss = str.split(",");
			Object[] rs = new Object[ss.length];
			for (int i=0;i<ss.length;i++)
				rs[i] = Common.doubleOf(ss[i], 0);
			return rs;
		}

		return new Object[] {Common.doubleOf(str, 0)};
	}

	public List<FeeDefine> listFeeRate(Long platformId, Long productId)
	{
		String sql = "select * from t_product_fee_define where valid is null and product_id = ? and platform_id = ? order by begin, end, pay, insure";

		return jdbc.query(sql, new RowMapper<FeeDefine>()
		{
			@Override
			public FeeDefine mapRow(ResultSet rs, int j) throws SQLException
			{
				return feeRateOf(rs, false);
			}

		}, productId, platformId);
	}

	public void saveFeeRate(Long platformId, Long productId, List<FeeDefine> list)
	{
		String insert = "insert into t_product_fee_define(platform_id, product_id, pay, insure, sale_fee, upper_bonus, sale_bonus, unit, freeze, begin, end) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		String update = "replace into t_product_fee_define(id, platform_id, product_id, pay, insure, sale_fee, upper_bonus, sale_bonus, unit, freeze, begin, end) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		for (FeeDefine fd : list)
		{
			String sf = Common.trimStringOf(fd.getSaleFee());
			String ub = Common.trimStringOf(fd.getUpperBonus());
			String sb = Common.trimStringOf(fd.getSaleBonus());

			if (Common.isEmpty(sf))
				sf = null;
			if (Common.isEmpty(ub))
				ub = null;
			if (Common.isEmpty(sb))
				sb = null;

			if (sf != null || ub != null || sb != null)
			{
				Map map = fd.getFactors();
				if (fd == null)
					jdbc.update(insert, platformId, productId, map == null ? null : map.get("pay"), map == null ? null : map.get("insure"), sf, ub, sb, fd.getUnit(), fd.getFreeze(), fd.getBegin(), fd.getEnd());
				else
					jdbc.update(update, fd.getId(), platformId, productId, map == null ? null : map.get("pay"), map == null ? null : map.get("insure"), sf, ub, sb, fd.getUnit(), fd.getFreeze(), fd.getBegin(), fd.getEnd());
			}
			else if (fd.getId() != null)
			{
				jdbc.update("delete from t_product_fee_define where id = ?", fd.getId());
			}
		}
	}

	public List<FeeDefine> listFeeRate(Long platformId, Long productId, Map rs)
	{
		StringBuffer sql = new StringBuffer();
		sql.append("select * from t_product_fee_define where valid is null and product_id = ? and platform_id = ?");

		String pay = Common.trimStringOf(rs.get("pay"));
		String insure = Common.trimStringOf(rs.get("insure"));

		sql.append(" and (pay is null or pay = '" + pay + "')");
		sql.append(" and (insure is null or insure = '" + insure + "')");

		return jdbc.query(sql.toString(), new RowMapper<FeeDefine>()
		{
			@Override
			public FeeDefine mapRow(ResultSet rs, int j) throws SQLException
			{
				return feeRateOf(rs, true);
			}

		}, productId, platformId);
	}

	private FeeDefine feeRateOf(ResultSet rs, boolean tran) throws SQLException
	{
		Long platformId = rs.getLong("platform_id");
		Long productId = rs.getLong("product_id");

		String pay = rs.getString("pay");
		String insure = rs.getString("insure");

		int freeze = Common.intOf(rs.getObject("freeze"), 0);
		int unit = Common.intOf(rs.getObject("unit"), 0);

		Date begin = rs.getDate("begin");
		Date end = rs.getDate("end");

		FeeDefine pc = new FeeDefine();
		pc.id = rs.getLong("id");
		pc.platformId = platformId;
		pc.productId = productId;
		pc.begin = begin;
		pc.end = end;
		pc.freeze = freeze;
		pc.unit = unit;
		pc.saleFee = rs.getString("sale_fee");
		pc.saleBonus = rs.getString("sale_bonus");
		pc.upperBonus = rs.getString("upper_bonus");

		if (tran)
		{
			pc.saleFee = valOf((String)pc.saleFee);
			pc.saleBonus = valOf((String)pc.saleBonus);
			pc.upperBonus = valOf((String)pc.upperBonus);
		}

		Map m = new JSONObject();
		m.put("pay", pay);
		m.put("insure", insure);
		pc.setFactors(m);

		return pc;
	}

	public Long prepare(Fee c)
	{
		c.setId(tools.nextId("fee"));

		jdbc.update("insert into t_product_fee(id, biz_type, biz_id, biz_no, product_id, vendor_id, amount, type, unit, estimate, freeze, pay, status, platform_id, payer, drawer, auto, memo, extra, create_time) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
				c.getId(), c.getBizType(), c.getBizId(), c.getBizNo(), c.getProductId(), c.getVendorId(), c.getAmount(), c.getType(), c.getUnit(), c.getEstimate(), c.getFreeze(), null, 0, c.getPlatformId(), c.getPayer(), c.getDrawer(), c.isAuto() ? "Y" : "N", c.getMemo(), JSON.toJSONString(c.getExtra()), c.getCreateTime());

		return c.getId();
	}

	public List<Fee> loadFeeReady(Long platformId, Long vendorId, String bizNo)
	{
		return jdbc.query("select * from t_product_fee where platform_id = ? and vendor_id = ? and biz_no = ? and auto = 'Y' and status = 0 and estimate <= ?", new RowMapper<Fee>()
		{
			@Override
			public Fee mapRow(ResultSet rs, int j) throws SQLException
			{
				return feeOf(rs);
			}

		}, platformId, vendorId, bizNo, new Date());
	}

	public Fee loadFee(Long id)
	{
		return jdbc.query("select * from t_product_fee where status in (0,9) and estimate <= now() and id = ?", new ResultSetExtractor<Fee>()
		{
			@Override
			public Fee extractData(ResultSet rs) throws SQLException, DataAccessException
			{
				return feeOf(rs);
			}
		}, id);
	}

	public List<Fee> findFee(Long platformId, Long vendorId, String bizNo)
	{
		return jdbc.query("select * from t_product_fee where platform_id = ? and vendor_id = ? and biz_no = ?", new RowMapper<Fee>()
		{
			@Override
			public Fee mapRow(ResultSet rs, int j) throws SQLException
			{
				return feeOf(rs);
			}

		}, platformId, vendorId, bizNo);
	}

	public List<Fee> findFee(Integer bizType, Long bizId)
	{
		return jdbc.query("select * from t_product_fee where biz_type = ? and biz_id = ?", new RowMapper<Fee>()
		{
			@Override
			public Fee mapRow(ResultSet rs, int j) throws SQLException
			{
				return feeOf(rs);
			}

		}, bizType, bizId);
	}

	public void pay(Long id, int status, Date time)
	{
		jdbc.update("update t_product_fee set pay = ?, status = ? where id = ?", time, status, id);
	}

	public static Fee feeOf(ResultSet c) throws SQLException
	{
		if (c == null)
			return null;

		Fee r = new Fee();

		r.id = c.getLong("id");
		r.platformId = Common.toLong(c.getObject("platform_id"));
		r.payer = Common.toLong(c.getObject("payer"));
		r.drawer = Common.toLong(c.getObject("drawer"));

		r.productId = Common.trimStringOf(c.getObject("product_id"));
		r.vendorId = Common.toLong(c.getObject("vendor_id"));
		r.bizNo = Common.trimStringOf(c.getObject("biz_no"));
		r.memo = Common.trimStringOf(c.getObject("memo"));

		r.extra = JSON.parseObject(c.getString("extra"));

		r.amount = Common.doubleOf(c.getObject("amount"), 0);
		r.auto = Common.boolOf(c.getObject("auto"), false);
		r.estimate = Common.dateOf(c.getObject("estimate"));
		r.type = Common.intOf(c.getObject("type"), 0);
		r.unit = Common.intOf(c.getObject("unit"), 1);
		r.freeze = Common.intOf(c.getObject("freeze"), 0);

		r.status = Common.intOf(c.getObject("status"), 9); //

		r.payTime = Common.dateOf(c.getObject("pay")); //
		r.createTime = Common.dateOf(c.getObject("create_time"), new Date());

		return r;
	}
}
