package lerrain.service.user.dao;

import com.alibaba.fastjson.JSONObject;
import lerrain.service.common.Log;
import lerrain.service.common.ServiceTools;
import lerrain.service.user.AppUser;
import lerrain.tool.Common;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class AppDao
{
	@Autowired
	JdbcTemplate jdbc;

	Long originalId;

	@PostConstruct
	private void init()
	{
		originalId = jdbc.queryForObject("select max(original_id) from t_app_user", Long.class);

		if (originalId == null)
			originalId = 701000000L;
	}

	public AppUser find(String userKey, String appCode)
	{
		try
		{
			return jdbc.queryForObject("select * from t_app_user where user_key = ? and app_code = ? limit 0, 1", new RowMapper<AppUser>()
			{
				@Override
				public AppUser mapRow(ResultSet tc, int arg1) throws SQLException
				{
					AppUser user = new AppUser();
					user.setOriginalId(Common.toLong(tc.getString("original_id")));
					user.setUserId(Common.toLong(tc.getString("user_id")));

					String info = tc.getString("info");
					if (info != null)
						user.setInfo(JSONObject.parseObject(info));

					return user;
				}
			}, userKey, appCode);
		}
		catch (Exception e)
		{
			Log.error(e);
			return null;
		}
	}

	public synchronized AppUser newUser(String userKey, String appCode)
	{
		AppUser user = find(userKey, appCode);
		if (user == null)
		{
			originalId++;
			jdbc.update("insert into t_app_user(user_key, app_code, original_id, create_time, creator, update_time, updater) values(?,?,?,now(),'system',now(),'system')", userKey, appCode, originalId);

			user = new AppUser();
			user.setOriginalId(originalId);
		}

		return user;
	}

}
