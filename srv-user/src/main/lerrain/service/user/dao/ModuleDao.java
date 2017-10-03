package lerrain.service.user.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lerrain.service.user.Module;
import lerrain.service.user.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class ModuleDao
{
	@Autowired
	JdbcTemplate jdbc;

	public List<Module> loadAll()
	{
		return jdbc.query("select * from s_module b order by b.parent_id, b.sequence, b.module_id", new RowMapper<Module>()
		{
			@Override
			public Module mapRow(ResultSet tc, int arg1) throws SQLException
			{
				Module module = new Module();
				module.setId(tc.getLong("module_id"));
				module.setCode(tc.getString("code"));
				module.setName(tc.getString("name"));
				module.setLink(tc.getString("link"));
				module.setParentId(tc.getLong("parent_id"));

				Integer seq = tc.getInt("sequence");
				module.setSequence(seq == null ? 10000 : seq);

				return module;
			}
		});
	}

	public List<Module> getRoleModule(Role role, final Map<Object, Module> res)
	{
		return jdbc.query("select b.module_id from s_role_module b where b.role_id = ?", new Object[] {role.getId()}, new RowMapper<Module>()
		{
			@Override
			public Module mapRow(ResultSet tc, int arg1) throws SQLException
			{
				return res.get(tc.getString("module_id"));
			}
		});
	}
}
