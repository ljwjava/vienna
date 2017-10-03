package lerrain.service.proposal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class ProductDao
{
	@Autowired
	JdbcTemplate jdbc;
	
	public List<Product> loadClauses(Long platformId)
	{
		return jdbc.query("select * from t_proposal_product where platform_id = ?", new RowMapper<Product>()
		{
			@Override
			public Product mapRow(ResultSet m, int arg1) throws SQLException
			{
				Product prd = new Product();
				prd.setId(m.getString("product_id"));
				prd.setName(m.getString("name"));
				prd.setLogo(m.getString("logo"));
				prd.setRemark(m.getString("remark"));
				prd.setTag(m.getString("tag"));
				prd.setType(m.getInt("type"));

				return prd;
			}
		}, platformId);
	}
}
