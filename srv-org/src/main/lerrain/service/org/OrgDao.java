package lerrain.service.org;

import lerrain.tool.Common;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class OrgDao
{
	@Autowired
	JdbcTemplate jdbc;

	public Org loadOrg(Long orgId)
	{
		return jdbc.queryForObject("select * from t_org where id = ?", new RowMapper<Org>()
		{
			@Override
			public Org mapRow(ResultSet tc, int arg1) throws SQLException
			{
				Org org = new Org();
				org.setId(tc.getLong("id"));
				org.setName(tc.getString("name"));
				org.setParentId(tc.getLong("parent_id"));

				return org;
			}
		}, orgId);
	}
}
