package lerrain.service.org;

import lerrain.tool.Common;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class CompanyDao
{
	@Autowired
	JdbcTemplate jdbc;

	public Company loadCompany(Long companyId)
	{
		return jdbc.queryForObject("select * from t_company where id = ?", new RowMapper<Company>()
		{
			@Override
			public Company mapRow(ResultSet tc, int arg1) throws SQLException
			{
				Company company = new Company();
				company.setId(tc.getLong("id"));
				company.setName(tc.getString("name"));

				return company;
			}
		}, companyId);
	}

}
