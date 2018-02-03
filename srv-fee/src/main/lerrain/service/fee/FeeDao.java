package lerrain.service.fee;

import com.alibaba.fastjson.JSON;
import lerrain.service.common.ServiceTools;
import lerrain.service.fee.function.JsonOf;
import lerrain.tool.Common;
import lerrain.tool.formula.Formula;
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
import java.util.*;

@Repository
public class FeeDao
{
	@Autowired JdbcTemplate jdbc;
	@Autowired ServiceTools tools;

	@Autowired
	PlatformFee platformFee;

	public Long prepare(Fee c)
	{
		c.setId(tools.nextId("fee"));

		jdbc.update("insert into t_fee(id, biz_no, product_id, amount, type, unit, estimate, freeze, pay, status, platform_id, drawee_type, drawee, payee_type, payee, auto, memo, extra, create_time) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
				c.getId(), c.getBizNo(), c.getProductId(), c.getAmount(), c.getType(), c.getUnit(), c.getEstimate(), c.getFreeze(), null, 0, c.getPlatformId(), c.getDraweeType(), c.getDrawee(), c.getPayeeType(), c.getPayee(), c.isAuto() ? "Y" : "N", c.getMemo(), JSON.toJSONString(c.getExtra()), c.getCreateTime());

		return c.getId();
	}

	public List<Fee> loadFeeReady()
	{
		List<Fee> r = null;

		List<Map<String, Object>> list = jdbc.queryForList("select * from t_fee where auto = 'Y' and status = 0 and estimate <= now()");
		if (list != null)
		{
			r = new ArrayList<>();
			for (Map<String, Object> map : list)
				r.add(Fee.feeOf(map));
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

	public FeeGrp loadFeeDefine()
	{
//		final Map<String, List<FeeDefine>> m = new HashMap<>();
		final FeeGrp feeGrp = new FeeGrp();

		String sql = "select * from t_fee_define where valid is null order by platform_id, agency_id, `group`, product_id, pay_freq, pay_period, begin, end";

		jdbc.query(sql, new RowCallbackHandler()
		{
			@Override
			public void processRow(ResultSet rs) throws SQLException
			{
				Long platformId = rs.getLong("platform_id");
				Long productId = rs.getLong("product_id");
				Long agencyId = rs.getLong("agency_id");
				String group = rs.getString("group");

				String payFreq = rs.getString("pay_freq");
				String payPeriod = rs.getString("pay_period");
				String insure = rs.getString("insure");

//				String key = platformId + "/" + agencyId + "/" + group + "/" + productId + "/" + payFreq + "/" + payPeriod;
//				List<FeeDefine> list = m.get(key);
//				if (list == null)
//				{
//					list = new ArrayList<FeeDefine>();
//					m.put(key, list);
//				}

				int freeze = Common.intOf(rs.getObject("freeze"), 0);
				int unit = Common.intOf(rs.getObject("unit"), 0);

				Date begin = rs.getDate("begin");
				Date end = rs.getDate("end");

				if (unit > 0)
				{
					FeeDefine pc = new FeeDefine(begin, end, freeze, unit);

					pc.f1 = valOf(rs.getString("f1"));
					pc.f2 = valOf(rs.getString("f2"));
					pc.f3 = valOf(rs.getString("f3"));
					pc.f4 = valOf(rs.getString("f4"));
					pc.f5 = valOf(rs.getString("f5"));

					pc.setMemo(rs.getString("memo"));

					feeGrp.addKey(platformId + "/" + agencyId + "/" + group + "/" + productId, new Object[] {payFreq, payPeriod, insure}, pc);
				}
			}

		});

		return feeGrp;
	}

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

	public PlatformFee loadPlatformScript()
	{
		platformFee.reset();

		jdbc.query("select * from t_fee_platform order by platform_id, begin desc", new RowCallbackHandler()
		{
			@Override
			public void processRow(ResultSet rs) throws SQLException
			{
				Long platformId = rs.getLong("platform_id");
				Formula pay = Script.scriptOf(rs.getString("pay"));
				Formula calc = Script.scriptOf(rs.getString("calc"));
				Formula charge = Script.scriptOf(rs.getString("charge"));


				platformFee.setVals(platformId, JSON.parseObject(rs.getString("const")));
				platformFee.add(platformId, rs.getDate("begin"), pay, calc, charge);
			}
		});

		return platformFee;
	}

	public List<Map<String, Object>> loadFeeDefine(Long platformId, Long productId)
	{
		String sql = "select * from t_fee_define where valid is null and product_id = ? and platform_id = ? order by begin, end, agency_id, `group`, pay_freq, pay_period";

		return jdbc.query(sql, new RowMapper<Map<String, Object>>()
		{
			@Override
			public Map<String, Object> mapRow(ResultSet rs, int j) throws SQLException
			{
				Map<String, Object> m = new HashMap();

				int num = rs.getMetaData().getColumnCount();
				for (int i = 1; i <= num; i++)
				{
					String key = rs.getMetaData().getColumnLabel(i);
					Object val = rs.getObject(i);

					int pos = key.indexOf("_");
					while (pos >= 0)
					{
						try
						{
							key = key.substring(0, pos) + key.substring(pos + 1, pos + 2).toUpperCase() + key.substring(pos + 2);
						}
						catch (Exception e)
						{
							break;
						}

						pos = key.indexOf("_");
					}

					m.put(key, val);
				}

				m.put("f1", valOf((String)m.get("f1")));
				m.put("f2", valOf((String)m.get("f2")));
				m.put("f3", valOf((String)m.get("f3")));
				m.put("f4", valOf((String)m.get("f4")));
				m.put("f5", valOf((String)m.get("f5")));

				return m;
			}

		}, productId, platformId);
	}
}
