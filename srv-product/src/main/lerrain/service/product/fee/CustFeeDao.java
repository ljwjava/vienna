package lerrain.service.product.fee;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import lerrain.service.common.ServiceTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
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

		return jdbc.query(sql, new RowMapper<CustFeeDefine>()
		{
			@Override
			public CustFeeDefine mapRow(ResultSet rs, int j) throws SQLException
			{
				return feeRateOf(rs);
			}

		}, productId, schemeId);
	}

	public void saveFeeRate(Long schemeId, Long productId, List<CustFeeDefine> list)
	{
		String update = "replace into t_product_fee_cust(scheme_id, product_id, content, create_time, update_time) values(?, ?, ?, now(), now())";

		JSONArray rs = new JSONArray();
		for (CustFeeDefine fd : list)
			rs.add(JSON.toJSONString(fd));

		jdbc.update(update, schemeId, productId, rs.toJSONString());
	}

	private CustFeeDefine feeRateOf(ResultSet rs) throws SQLException
	{
		String str = rs.getString("content");
		CustFeeDefine pc = JSON.toJavaObject(JSON.parseObject(str), CustFeeDefine.class);

		Long schemeId = rs.getLong("scheme_id");
		Long productId = rs.getLong("product_id");
		pc.setSchemeId(schemeId);
		pc.setProductId(productId);

		return pc;
	}
}
