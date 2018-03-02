package lerrain.service.product.fee;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lerrain.service.common.Log;
import lerrain.service.common.ServiceTools;
import lerrain.tool.Common;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
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

	public List<FeeRate> listFeeRate(Long platformId, Long agencyId, Long productId)
	{
		String sql = "select * from t_fee_define where valid is null and product_id = ? and platform_id = ? order by begin, end, agency_id, `group`, pay_freq, pay_period";

		return jdbc.query(sql, new RowMapper<FeeRate>()
		{
			@Override
			public FeeRate mapRow(ResultSet rs, int j) throws SQLException
			{
				return feeRateOf(rs);
			}

		}, productId, platformId);
	}

	public List<FeeRate> listFeeRate(Long platformId, Long agencyId, Long productId, String group, Map rs)
	{
		StringBuffer sql = new StringBuffer();
		sql.append("select * from t_fee_define where valid is null and product_id = ? and platform_id = ? and agency_id = ? and `group` = ?");

		String payFreq = "year";
		String payPeriod = Common.trimStringOf(rs.get("payPeriod"));
		String insure = Common.trimStringOf(rs.get("insure"));

		if (payFreq != null)
			sql.append(" and (pay_freq is null or pay_freq = '" + payFreq + "')");
		if (payPeriod != null)
			sql.append(" and (pay_period is null or pay_period = '" + payPeriod + "')");
		if (insure != null)
			sql.append(" and (insure is null or insure = '" + insure + "')");

		Log.debug(sql.toString());
		Log.debug("%d, %d, %d, %s", platformId, agencyId, productId, group);

		return jdbc.query(sql.toString(), new RowMapper<FeeRate>()
		{
			@Override
			public FeeRate mapRow(ResultSet rs, int j) throws SQLException
			{
				return feeRateOf(rs);
			}

		}, productId, platformId, agencyId, group);
	}

	private FeeRate feeRateOf(ResultSet rs) throws SQLException
	{
		Long platformId = rs.getLong("platform_id");
		Long productId = rs.getLong("product_id");
		Long agencyId = rs.getLong("agency_id");
		String group = rs.getString("group");

		String payFreq = rs.getString("pay_freq");
		String payPeriod = rs.getString("pay_period");
		String insure = rs.getString("insure");

		int freeze = Common.intOf(rs.getObject("freeze"), 0);
		int unit = Common.intOf(rs.getObject("unit"), 0);

		Date begin = rs.getDate("begin");
		Date end = rs.getDate("end");

		FeeRate pc = new FeeRate();
		pc.platformId = platformId;
		pc.productId = productId;
		pc.agencyId = agencyId;
		pc.group = group;
		pc.begin = begin;
		pc.end = end;
		pc.freeze = freeze;
		pc.unit = unit;
		pc.f1 = valOf(rs.getString("f1"));
		pc.f2 = valOf(rs.getString("f2"));
		pc.f3 = valOf(rs.getString("f3"));
		pc.f4 = valOf(rs.getString("f4"));
		pc.f5 = valOf(rs.getString("f5"));

		Map m = new JSONObject();
		m.put("payFreq", payFreq);
		m.put("payPeriod", payPeriod);
		m.put("insure", insure);
		pc.setFactors(m);

		return pc;
	}

	public Long prepare(Fee c)
	{
		c.setId(tools.nextId("fee"));

		jdbc.update("insert into t_fee(id, biz_no, product_id, amount, type, unit, estimate, freeze, pay, status, platform_id, payee, auto, memo, extra, create_time) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
				c.getId(), c.getBizNo(), c.getProductId(), c.getAmount(), c.getType(), c.getUnit(), c.getEstimate(), c.getFreeze(), null, 0, c.getPlatformId(), c.getDrawer(), c.isAuto() ? "Y" : "N", c.getMemo(), JSON.toJSONString(c.getExtra()), c.getCreateTime());

		return c.getId();
	}

	public List<Fee> loadFeeReady(Long platformId, Long productId, String bizNo)
	{
		List<Fee> r = null;

		List<Map<String, Object>> list = jdbc.queryForList("select * from t_fee where platform_id = ? and product_id = ? and biz_no = ? and auto = 'Y' and status = 0 and estimate <= ?", platformId, productId, bizNo, new Date());
		if (list != null)
		{
			r = new ArrayList<>();
			for (Map<String, Object> map : list)
			{
				map.put("drawer", map.get("payee"));
				r.add(Fee.feeOf(map));
			}
		}

		return r;
	}

	public Fee loadFee(Long id)
	{
		return Fee.feeOf(jdbc.queryForMap("select * from t_fee where status in (0,9) and estimate <= now() and id = ?", id));
	}

	public void pay(Long id, int status, Date time)
	{
		jdbc.update("update t_fee set pay = ?, status = ? where id = ?", time, status, id);
	}

}
