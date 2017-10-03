package lerrain.service.user.dao;

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
		String sql = "select count(*) from s_login where login_name = ?";
		return jdbc.queryForObject(sql, new Object[] {loginName}, Integer.class) > 0;
	}
	
	public String findUserId(String loginName)
	{
		String sql = "select user_id from s_login where login_name = ?";
		return jdbc.queryForObject(sql, new Object[] {loginName}, String.class);
	}
	
	public User load(String userId)
	{
		String sql;
		
		sql = "select u.* from s_user u where u.user_id = ?";
		Map<String, Object> m = jdbc.queryForMap(sql, new Object[] {userId});
		
		User user = new User();
		user.setId(Common.toLong(m.get("user_id")));
//		user.setPassword((String) m.get("password"));
		user.setName((String) m.get("user_name"));
		user.setPlatformId(Common.toLong(m.get("platform_id")));
		user.setLoginTime((Date) m.get("login_time"));
		user.setStatus(Common.intOf(m.get("status"), 0));

		sql = "select a.role_id from s_user_role a, s_role b where a.role_id = b.role_id and a.user_id = ?";
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
	public String verify(String account, String password)
	{
		String sql = "select a.user_id, a.status from s_user a, s_login b where a.user_id = b.user_id and b.login_name = ? and a.password = ?";
		
		String userId = null;
		int status = 0;
		
		try
		{
			Map<String, Object> r = jdbc.queryForMap(sql, new Object[] {account, Common.md5Of(password)});
			if (r == null || r.isEmpty())
				throw new RuntimeException("用户名或密码错误");
			
			userId = Common.trimStringOf(r.get("USER_ID"));
			status = Common.intOf(r.get("STATUS"), 0);

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
	
	public void updatePassword(String userId, String password)
	{
		String sql = "update s_user set password = ? where user_id = ?";
		jdbc.update(sql, Common.md5Of(password), userId);
	}

	public void updateLoginTime(String userId, Date time)
	{
		String sql = "update s_user set login_time = ? where user_id = ?";
		jdbc.update(sql, new Object[] {time, userId});
	}
	
	public void updateStatus(String[] usersId, int status)
	{
		String sql = "update s_user set status = ? where user_id = ?";
		for (String userId : usersId)
			jdbc.update(sql, new Object[] {status, userId});
	}
	
	public void updatePassword(String[] usersId, String password)
	{
		String sql = "update s_user set password = ? where user_id = ?";
		for (String userId : usersId)
			jdbc.update(sql, new Object[] {Common.md5Of(password), userId}, null);
	}
}
