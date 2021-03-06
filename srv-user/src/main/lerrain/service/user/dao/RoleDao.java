package lerrain.service.user.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import lerrain.service.user.Role;
import lerrain.service.user.service.ModuleService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class RoleDao
{
	@Autowired
	ModuleService moduleService;
	
	@Autowired
	JdbcTemplate jdbc;
	
	public List<Role> loadAll()
	{
		List<Role> res = jdbc.query("select b.* from t_role b", new RowMapper<Role>()
		{
			@Override
			public Role mapRow(ResultSet tc, int arg1) throws SQLException
			{
				Role role = new Role();
				role.setId(tc.getLong("role_id"));
				role.setCode(tc.getString("code"));
				role.setName(tc.getString("name"));
				
				return role;
			}
		});

		return res;
	}
}
