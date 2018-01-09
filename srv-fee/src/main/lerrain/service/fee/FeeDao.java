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

	public Map<String, List<FeeDefine>> loadFeeDefine()
	{
		final Map<String, List<FeeDefine>> m = new HashMap<>();

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

				String key = platformId + "/" + agencyId + "/" + group + "/" + productId + "/" + payFreq + "/" + payPeriod;

				List<FeeDefine> list = m.get(key);
				if (list == null)
				{
					list = new ArrayList<FeeDefine>();
					m.put(key, list);
				}

				int freeze = Common.intOf(rs.getObject("freeze"), 0);
				int unit = Common.intOf(rs.getObject("unit"), 0);
				int type = Common.intOf(rs.getObject("type"), 0);

				Date begin = rs.getDate("begin");
				Date end = rs.getDate("end");

				if (unit > 0)
				{
					FeeDefine pc = new FeeDefine(begin, end, freeze, unit, type);

					pc.a1 = Common.toDouble(rs.getObject("a1"));
					pc.a2 = Common.toDouble(rs.getObject("a2"));
					pc.a3 = Common.toDouble(rs.getObject("a3"));
					pc.a4 = Common.toDouble(rs.getObject("a4"));

					pc.b1 = JSON.parseArray(rs.getString("b1"));
					pc.b2 = JSON.parseArray(rs.getString("b2"));
					pc.b3 = JSON.parseArray(rs.getString("b3"));
					pc.b4 = JSON.parseArray(rs.getString("b4"));

					pc.c1 = JSON.parseObject(rs.getString("c1"));
					pc.c2 = JSON.parseObject(rs.getString("c2"));
					pc.c3 = JSON.parseObject(rs.getString("c3"));
					pc.c4 = JSON.parseObject(rs.getString("c4"));

					pc.setMemo(rs.getString("memo"));

					list.add(pc);
				}
			}

		});

		return m;
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
}
