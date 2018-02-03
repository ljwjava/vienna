package lerrain.service.user.dao;

import com.alibaba.fastjson.JSON;
import lerrain.service.user.Constant;
import lerrain.service.user.Role;
import lerrain.service.user.User;
import lerrain.service.user.service.RoleService;
import lerrain.tool.Common;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
public class UserDao
{
	@Autowired
	RoleService roleService;
	
	@Autowired
	JdbcTemplate jdbc;
	
	public boolean isExisted(String loginName)
	{
		String sql = "select count(*) from t_login where login_name = ?";
		return jdbc.queryForObject(sql, new Object[] {loginName}, Integer.class) > 0;
	}
	
	public String findUserId(String loginName)
	{
		String sql = "select user_id from t_login where login_name = ?";
		return jdbc.queryForObject(sql, new Object[] {loginName}, String.class);
	}
	
	public User load(Long userId)
	{
		String sql;
		
		sql = "select u.* from t_user u where u.user_id = ?";
		Map<String, Object> m = jdbc.queryForMap(sql, new Object[] {userId});
		
		User user = new User();
		user.setId(Common.toLong(m.get("user_id")));
//		user.setPassword((String) m.get("password"));
		user.setName((String) m.get("user_name"));
		user.setLoginTime((Date) m.get("login_time"));
		user.setStatus(Common.intOf(m.get("status"), 0));

		sql = "select a.role_id from t_user_role a, t_role b where a.role_id = b.role_id and a.user_id = ?";
		user.setRole(jdbc.query(sql, new Object[] {userId}, new RowMapper<Role>()
		{
			@Override
			public Role mapRow(ResultSet rs, int arg1) throws SQLException 
			{
				return roleService.getRole(rs.getLong("role_id"));
			}
		}));

		return user;
	}
	
	/**
	 * 校验用户名密码
	 * @param account
	 * @param password
	 * @return userId
	 */
	public Long verify(String account, String password)
	{
		String sql = "select a.user_id, a.status from t_user a, t_login b where a.user_id = b.user_id and b.login_name = ? and a.password = ?";

		Long userId = null;
		int status = 0;
		
		try
		{
			Map<String, Object> r = jdbc.queryForMap(sql, new Object[] {account, Common.md5Of(password)});
			if (r == null || r.isEmpty())
				throw new RuntimeException("用户名或密码错误");
			
			userId = Common.toLong(r.get("user_id"));
			status = Common.intOf(r.get("status"), 0);

			if (userId == null)
				return null;
		}
		catch (EmptyResultDataAccessException e1)
		{
			throw new RuntimeException("用户名或密码错误");
		}
		catch (Exception e)
		{
			throw new RuntimeException("系统错误", e);
		}

		if (status == Constant.STATUS_INACTIVE)
			throw new RuntimeException("该用户未激活");
		else if (status == Constant.STATUS_FORBIDDEN)
			throw new RuntimeException("该用户已被禁用");
		else if (status == Constant.STATUS_UNAVAILABLE)
			throw new RuntimeException("该用户已失效");
		
		return userId;
	}

	public int verify(Long userId, String password)
	{
		String sql = "select count(*) from t_user a where a.valid is null and a.user_id = ? and a.password = ?";

		try
		{
			int num = jdbc.queryForObject(sql, Integer.class, userId, Common.md5Of(password));
			return num == 1 ? 1 : 0;
		}
		catch (Exception e)
		{
			return -1;
		}
	}
	
	public void updatePassword(Long userId, String password)
	{
		String sql = "update t_user set password = ? where user_id = ?";
		jdbc.update(sql, Common.md5Of(password), userId);
	}

	public void updateLoginTime(Long userId, Date time)
	{
		String sql = "update t_user set login_time = ? where user_id = ?";
		jdbc.update(sql, new Object[] {time, userId});
	}
	
	public void updateStatus(Long[] usersId, int status)
	{
		String sql = "update t_user set status = ? where user_id = ?";
		for (Long userId : usersId)
			jdbc.update(sql, new Object[] {status, userId});
	}
	
	public void updatePassword(Long[] usersId, String password)
	{
		String sql = "update t_user set password = ? where user_id = ?";
		for (Long userId : usersId)
			jdbc.update(sql, new Object[] {Common.md5Of(password), userId}, null);
	}

	public int count(String search)
	{
		StringBuffer sql = new StringBuffer("select count(*) from t_user where valid is null");
		if (search != null && !"".equals(search))
			sql.append(" and user_name like '%" + search + "%' ");

		return jdbc.queryForObject(sql.toString(), Integer.class);
	}

	public List<User> list(String search, int from, int number)
	{
		StringBuffer sql = new StringBuffer("select * from t_user where valid is null");
		if (search != null && !"".equals(search))
			sql.append(" and user_name like '%" + search + "%' ");
		sql.append(" order by update_time desc");
		sql.append(" limit ?, ?");

		return jdbc.query(sql.toString(), new Object[] {from, number}, new RowMapper<User>()
		{
			@Override
			public User mapRow(ResultSet m, int arg1) throws SQLException
			{
				User p = new User();
				p.setId(m.getLong("user_id"));
				p.setName(m.getString("user_name"));
				p.setStatus(m.getInt("status"));
				p.setExtra(JSON.parseObject(m.getString("extra")));

				return p;
			}
		});
	}
}
