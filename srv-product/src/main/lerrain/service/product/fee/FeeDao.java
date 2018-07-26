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
import java.util.*;

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

	public List<FeeDefine> listFeeRate(Long schemeId, Long productId)
	{
		String sql = "select * from t_product_fee_define where valid is null and product_id = ? and scheme_id = ? order by begin, end, pay, insure, oth_factors";

		List<FeeDefine> l = jdbc.query(sql, new RowMapper<FeeDefine>()
		{
			@Override
			public FeeDefine mapRow(ResultSet rs, int j) throws SQLException
			{
				return feeRateOf(rs, false, null);
			}

		}, productId, schemeId);

		List<FeeDefine> lnew = new ArrayList<>();
		for(FeeDefine fd : l){
			if(fd != null)
				lnew.add(fd);
		}

		return lnew;
	}

	public void saveFeeRate(Long schemeId, Long productId, List<FeeDefine> list)
	{
		String insert = "insert into t_product_fee_define(scheme_id, product_id, pay, insure, oth_factors, sale_fee, upper_bonus, sale_bonus, unit, freeze, begin, end) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		String update = "replace into t_product_fee_define(id, scheme_id, product_id, pay, insure, oth_factors, sale_fee, upper_bonus, sale_bonus, unit, freeze, begin, end) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

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
				Map<String, Object> map = fd.getFactors();
				JSONObject othFactors = null;
				for(String key : map.keySet()) {
					if(!key.equalsIgnoreCase("pay") && !key.equalsIgnoreCase("insure")) {
						if(othFactors == null) othFactors = new JSONObject();
						othFactors.put(key, map.get(key));
					}
				}
				if (fd == null)
					jdbc.update(insert, schemeId, productId, map == null ? null : map.get("pay"), map == null ? null : map.get("insure"), othFactors == null ? null : othFactors, sf, ub, sb, fd.getUnit(), fd.getFreeze(), fd.getBegin(), fd.getEnd());
				else
					jdbc.update(update, fd.getId(), schemeId, productId, map == null ? null : map.get("pay"), map == null ? null : map.get("insure"), othFactors == null ? null : othFactors, sf, ub, sb, fd.getUnit(), fd.getFreeze(), fd.getBegin(), fd.getEnd());
			}
			else if (fd.getId() != null)
			{
				jdbc.update("delete from t_product_fee_define where id = ?", fd.getId());
			}
		}
	}

	public List<FeeDefine> listFeeRate(Long schemeId, Long productId, Map<String, Object> rs)
	{
		StringBuffer sql = new StringBuffer();
		sql.append("select * from t_product_fee_define where valid is null and product_id = ? and scheme_id = ?");

		String pay = Common.trimStringOf(rs.get("pay"));
		String insure = Common.trimStringOf(rs.get("insure"));
		final Map<String, Object> othFactors = new HashMap<>();
		for(String key : rs.keySet()) {
			if(!key.equalsIgnoreCase("pay") && !key.equalsIgnoreCase("insure")) {
				othFactors.put(key, rs.get(key));
			}
		}

		sql.append(" and (pay is null or pay = '" + pay + "')");
		sql.append(" and (insure is null or insure = '" + insure + "')");

		List<FeeDefine> l = jdbc.query(sql.toString(), new RowMapper<FeeDefine>()
		{
			@Override
			public FeeDefine mapRow(ResultSet rs, int j) throws SQLException
			{
				return feeRateOf(rs, true, othFactors);
			}

		}, productId, schemeId);

		List<FeeDefine> lnew = new ArrayList<>();
		for(FeeDefine fd : l){
			if(fd != null)
				lnew.add(fd);
		}

		return lnew;
	}

	/**
	 * 整理并过滤数据
	 * @param rs
	 * @param tran
	 * @param othFactors 为null时，返回所有；否则根据因子判断是否返回数据
	 * @return
	 * @throws SQLException
	 */
	private FeeDefine feeRateOf(ResultSet rs, boolean tran, Map<String, Object> othFactors) throws SQLException
	{
		Long schemeId = rs.getLong("scheme_id");
		Long productId = rs.getLong("product_id");

		String pay = rs.getString("pay");
		String insure = rs.getString("insure");
		String ofStr = rs.getString("oth_factors");	// 其他因子
		Map<String, Object> ofDB = new HashMap<>();
		if(!Common.isEmpty(ofStr)) {
			ofDB = JSON.parseObject(ofStr);
		}

		int freeze = Common.intOf(rs.getObject("freeze"), 0);
		int unit = Common.intOf(rs.getObject("unit"), 0);

		Date begin = rs.getDate("begin");
		Date end = rs.getDate("end");

		FeeDefine pc = new FeeDefine();
		pc.id = rs.getLong("id");
		pc.schemeId = schemeId;
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
		if(!Common.isEmpty(ofDB))
			m.putAll(ofDB);
		pc.setFactors(m);

		// 查询所有
		if(othFactors == null) {
			return pc;
		}

		// 若数据库配置有额外因子配置，但传参没有，则不返回这条佣金配置
		if(Common.isEmpty(othFactors) && !Common.isEmpty(ofDB)){
			return null;
		}

		// 判断所有因子是否符合
		if(!Common.isEmpty(ofDB)){
			for(String key : ofDB.keySet()) {
				String vDB = Common.trimStringOf(m.get(key));	// 佣金定义因子对应值
				String v = Common.trimStringOf(othFactors.get(key));
				// 若配置有值，且与传值不一致，则直接返回空
				if(!Common.isEmpty(vDB) && !vDB.equalsIgnoreCase(v)){
					return null;
				}
			}
		}

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
		return jdbc.queryForObject("select * from t_product_fee where status in (0,9) and estimate <= now() and id = ?", new RowMapper<Fee>()
		{
			@Override
			public Fee mapRow(ResultSet rs, int rowNum) throws SQLException
			{
				if(!rs.wasNull()){
					return feeOf(rs);
				} else {
					return null;
				}
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

	public List<Fee> findFeeLimit(String productId, Long vendorId, Integer bizType, Long bizId, String bizNo, Integer type, Integer unit, Date estimateB, Date estimateE, Boolean auto, Date payB, Date payE, Integer freezeMin, Integer freezeMax, Integer status, Long payer, Long drawer, Long start, Long limit)
	{
		String sql = "select pf.* from t_product_fee pf where 1 = 1";
		List<Object> params = new ArrayList<Object>();
		if(!Common.isEmpty(productId)){
			sql += " and product_id = ?";
			params.add(productId);
		}
		if(!Common.isEmpty(vendorId)){
			sql += " and vendor_id = ?";
			params.add(vendorId);
		}
		if(!Common.isEmpty(bizType)){
			sql += " and biz_type = ?";
			params.add(bizType);
		}
		if(!Common.isEmpty(bizId)){
			sql += " and biz_id = ?";
			params.add(bizId);
		}
		if(!Common.isEmpty(bizNo)){
			sql += " and biz_no = ?";
			params.add(bizNo);
		}
		if(!Common.isEmpty(type)){
			sql += " and type = ?";
			params.add(type);
		}
		if(!Common.isEmpty(unit)){
			sql += " and unit = ?";
			params.add(unit);
		}
		if(!Common.isEmpty(estimateB)){
			sql += " and estimate >= ?";
			params.add(estimateB);
		}
		if(!Common.isEmpty(estimateE)){
			sql += " and estimate < ?";
			params.add(estimateE);
		}
		if(!Common.isEmpty(auto)){
			sql += " and auto = ?";
			params.add(auto?"Y":"N");
		}
		if(!Common.isEmpty(payB)){
			sql += " and pay >= ?";
			params.add(payB);
		}
		if(!Common.isEmpty(payE)){
			sql += " and pay < ?";
			params.add(payE);
		}
		if(!Common.isEmpty(freezeMin)){
			sql += " and freeze >= ?";
			params.add(freezeMin);
		}
		if(!Common.isEmpty(freezeMax)){
			sql += " and freeze < ?";
			params.add(freezeMax);
		}
		if(!Common.isEmpty(status)){
			sql += " and status = ?";
			params.add(status);
		}
		if(!Common.isEmpty(payer)){
			sql += " and payer = ?";
			params.add(payer);
		}
		if(!Common.isEmpty(drawer)){
			sql += " and drawer = ?";
			params.add(drawer);
		}
		sql += " order by create_time desc, vendor_id, biz_id";
		if(!Common.isEmpty(start) && !Common.isEmpty(limit)) {
			sql += " limit ?, ?";
			start = Common.isEmpty(start) ? 0 : start;
			limit = Common.isEmpty(limit) ? 10 : limit;
			params.add(start);
			params.add(limit);
		}

		try {
			return jdbc.query(sql, params.toArray(), new RowMapper<Fee>() {
				@Override
				public Fee mapRow(ResultSet rs, int i) throws SQLException {
					return feeOf(rs);
				}
			});
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public Long countFee(String productId, Long vendorId, Integer bizType, Long bizId, String bizNo, Integer type, Integer unit, Date estimateB, Date estimateE, Boolean auto, Date payB, Date payE, Integer freezeMin, Integer freezeMax, Integer status, Long payer, Long drawer)
	{
		String sql = "select count(pf.id) cnt from t_product_fee pf where 1 = 1";
		List<Object> params = new ArrayList<Object>();
		if(!Common.isEmpty(productId)){
			sql += " and product_id = ?";
			params.add(productId);
		}
		if(!Common.isEmpty(vendorId)){
			sql += " and vendor_id = ?";
			params.add(vendorId);
		}
		if(!Common.isEmpty(bizType)){
			sql += " and biz_type = ?";
			params.add(bizType);
		}
		if(!Common.isEmpty(bizId)){
			sql += " and biz_id = ?";
			params.add(bizId);
		}
		if(!Common.isEmpty(bizNo)){
			sql += " and biz_no = ?";
			params.add(bizNo);
		}
		if(!Common.isEmpty(type)){
			sql += " and type = ?";
			params.add(type);
		}
		if(!Common.isEmpty(unit)){
			sql += " and unit = ?";
			params.add(unit);
		}
		if(!Common.isEmpty(estimateB)){
			sql += " and estimate >= ?";
			params.add(estimateB);
		}
		if(!Common.isEmpty(estimateE)){
			sql += " and estimate < ?";
			params.add(estimateE);
		}
		if(!Common.isEmpty(auto)){
			sql += " and auto = ?";
			params.add(auto?"Y":"N");
		}
		if(!Common.isEmpty(payB)){
			sql += " and pay >= ?";
			params.add(payB);
		}
		if(!Common.isEmpty(payE)){
			sql += " and pay < ?";
			params.add(payE);
		}
		if(!Common.isEmpty(freezeMin)){
			sql += " and freeze >= ?";
			params.add(freezeMin);
		}
		if(!Common.isEmpty(freezeMax)){
			sql += " and freeze < ?";
			params.add(freezeMax);
		}
		if(!Common.isEmpty(status)){
			sql += " and status = ?";
			params.add(status);
		}
		if(!Common.isEmpty(payer)){
			sql += " and payer = ?";
			params.add(payer);
		}
		if(!Common.isEmpty(drawer)){
			sql += " and drawer = ?";
			params.add(drawer);
		}

		try {
			Map<String, Object> map = jdbc.queryForMap(sql, params.toArray(), null);
			if(!Common.isEmpty(map)){
				return Common.toLong(map.get("cnt"));
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return 0L;
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
