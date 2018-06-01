package lerrain.service.product.fee;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lerrain.service.common.ServiceTools;
import lerrain.tool.Common;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
public class CustFeeDao
{
	@Autowired JdbcTemplate jdbc;
	@Autowired ServiceTools tools;

	public List<CustFeeDefine> listFeeRate(Long schemeId, Long productId)
	{
		String sql = "select * from t_product_fee_cust where valid is null and product_id = ? and scheme_id = ?";

		return jdbc.queryForObject(sql, new RowMapper<List<CustFeeDefine>>()
		{
			@Override
			public List<CustFeeDefine> mapRow(ResultSet rs, int j) throws SQLException
			{
				return feeRateOf(rs);
			}

		}, productId, schemeId);
	}

	public void saveFeeRate(Long schemeId, Long productId, Date begin, Date end, List<CustFeeDefine> list)
	{
		String update = "replace into t_product_fee_cust(scheme_id, product_id, `begin`, `end`, content, create_time, update_time) values(?, ?, ?, ?, ?, now(), now())";

		JSONArray rs = new JSONArray();
		for (CustFeeDefine fd : list)
			rs.add(JSON.toJSONString(fd));

		jdbc.update(update, schemeId, productId, begin, end, rs.toJSONString());
	}

	public CustFeeDefine findFeeRate(final Long schemeId, final Long productId, final Date time, final Map seek)
	{
		String sql = "select * from t_product_fee_cust where valid is null and product_id = ? and scheme_id = ?";

		return jdbc.queryForObject(sql, new RowMapper<CustFeeDefine>()
		{
			@Override
			public CustFeeDefine mapRow(ResultSet rs, int j) throws SQLException
			{
				String str = rs.getString("content");
				List<CustFeeDefine> list = JSON.parseArray(str, CustFeeDefine.class);

				for (CustFeeDefine cfd : list)
				{
					cfd.setSchemeId(rs.getLong("scheme_id"));
					cfd.setProductId(rs.getLong("product_id"));
					cfd.setBegin(rs.getDate("begin"));
					cfd.setEnd(rs.getDate("end"));

					if (cfd.match(time, seek))
						return cfd;
				}

				throw new RuntimeException(String.format("not found - scheme: %s product: %s @ %tT%tF - seek: %s", schemeId, productId, time, time, seek.toString()));

			}

		}, productId, schemeId);
	}

	private List<CustFeeDefine> feeRateOf(ResultSet rs) throws SQLException
	{
		String str = rs.getString("content");
		List<CustFeeDefine> list = JSON.parseArray(str, CustFeeDefine.class);

//		JSONArray list = JSON.parseArray(str);
//		for (int i=0;i<list.size();i++)
//		{
//			JSONObject json = list.getJSONObject(i);
//
//		}
//		CustFeeDefine pc = JSON.toJavaObject(JSON.parseObject(str), CustFeeDefine.class);

		for (CustFeeDefine cfd : list)
		{
			cfd.setSchemeId(rs.getLong("scheme_id"));
			cfd.setProductId(rs.getLong("product_id"));
			cfd.setBegin(rs.getDate("begin"));
			cfd.setEnd(rs.getDate("end"));
		}

		return list;
	}

	public List<CustFeeFactor> loadFeeDefineFactors()
	{
		final List<CustFeeFactor> r = new ArrayList<>();

		jdbc.query("select * from t_product_fee_factors", new RowCallbackHandler()
		{
			@Override
			public void processRow(ResultSet rs) throws SQLException
			{
				CustFeeFactor cff = new CustFeeFactor();
				cff.setWareId(rs.getLong("ware_id"));

				String[] products = rs.getString("products").split(",");
				List<Long> ids = new ArrayList<>();
				for (String productId : products)
					ids.add(Common.toLong(productId));
				cff.setProductIds(ids);

				JSONArray list = JSON.parseArray(rs.getString("content"));
				for (int i=0;i<list.size();i++)
				{
					JSONObject detail = list.getJSONObject(i);
					cff.setFactors(detail);
				}

				r.add(cff);
			}
		});

		return r;
	}
}
