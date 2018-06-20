package lerrain.service.varia;

import lerrain.service.common.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Repository
public class AgentDao
{
	@Autowired
	JdbcTemplate jdbc;

	public Map find(String certNo, String name)
	{
		try
		{
			Map map = jdbc.queryForObject("select * from t_agent1 where cert_no = ? and `name` = ? and flag = 1 limit 0, 1", new RowMapper<Map>()
			{
				@Override
				public Map mapRow(ResultSet tc, int arg1) throws SQLException
				{
					Map m = new HashMap();
					m.put("name", tc.getString("name"));
					m.put("gender", tc.getString("gender"));
					m.put("certNo", tc.getString("cert_no"));
					m.put("certfiNo", tc.getString("certfi_no"));
					m.put("certfiStatus", tc.getString("certfi_status"));
					m.put("bizNo", tc.getString("biz_no"));
					m.put("bizStatus", tc.getString("biz_status"));
					m.put("validDate", tc.getString("valid_date"));
					m.put("bizScope", tc.getString("biz_scope"));
					m.put("bizArea", tc.getString("biz_area"));
					m.put("company", tc.getString("company"));
					m.put("certfiNo2", tc.getString("certfi_no2"));
					m.put("certfiType", tc.getString("certfi_type"));
					m.put("certfiScope", tc.getString("certfi_scope"));
					m.put("certfiValidDate", tc.getString("certfi_valid_date"));

					return m;
				}
			}, certNo, name);

			Log.info(map);
			return map;
		}
		catch (Exception e)
		{
		}

		try
		{
			return jdbc.queryForObject("select * from t_agent where cert_no = ? and `name` = ? order by flag desc limit 0, 1", new RowMapper<Map>()
			{
				@Override
				public Map mapRow(ResultSet tc, int arg1) throws SQLException
				{
					Map m = new HashMap();

					int flag = tc.getInt("flag");
					if (flag == 0)
					{
						Date last = tc.getDate("time");
						Date now = new Date();

						if (now.getTime() - last.getTime() > 3600000L * 24 * 7)
							return null;

						return m;
					}

					m.put("name", tc.getString("name"));
					m.put("gender", tc.getString("gender"));
					m.put("certNo", tc.getString("cert_no"));
					m.put("certfiNo", tc.getString("certfi_no"));
					m.put("certfiStatus", tc.getString("certfi_status"));
					m.put("bizNo", tc.getString("biz_no"));
					m.put("bizStatus", tc.getString("biz_status"));
					m.put("validDate", tc.getString("valid_date"));
					m.put("bizScope", tc.getString("biz_scope"));
					m.put("bizArea", tc.getString("biz_area"));
					m.put("company", tc.getString("company"));
					m.put("certfiNo2", tc.getString("certfi_no2"));
					m.put("certfiType", tc.getString("certfi_type"));
					m.put("certfiScope", tc.getString("certfi_scope"));
					m.put("certfiValidDate", tc.getString("certfi_valid_date"));

					return m;
				}
			}, certNo, name);
		}
		catch (Exception e)
		{
			return null;
		}
	}

	public void save(Map m, int flag)
	{
		Long id = null;

		try
		{
			id = jdbc.queryForObject("select id from t_agent where cert_no = ? and `name` = ? and flag = 0", Long.class, m.get("certNo"), m.get("name"));
		}
		catch (Exception e)
		{
		}

		if (id == null)
		{
			jdbc.update("insert into t_agent(`name`, gender, cert_no, certfi_no, certfi_status, biz_no, biz_status, valid_date, biz_scope, biz_area, company, flag, `time`, file, certfi_no2, certfi_type, certfi_scope, certfi_valid_date, file2) values(?,?,?,?,?,?,?,?,?,?,?,?,now(),?,?,?,?,?,?)",
					m.get("name"),
					m.get("gender"),
					m.get("certNo"),
					m.get("certfiNo"),
					m.get("certfiStatus"),
					m.get("bizNo"),
					m.get("bizStatus"),
					m.get("validDate"),
					m.get("bizScope"),
					m.get("bizArea"),
					m.get("company"),
					flag,
					m.get("file"),
					m.get("certfiNo2"),
					m.get("certfiType"),
					m.get("certfiScope"),
					m.get("certfiValidDate"),
					m.get("file2")
			);
		}
		else if (flag == 0)
		{
			jdbc.update("update t_agent set `time` = now(), file = ? where id = ?", m.get("file"), id);
		}
		else
		{
			jdbc.update("update t_agent set `name`=?, gender=?, cert_no=?, certfi_no=?, certfi_status=?, biz_no=?, biz_status=?, valid_date=?, biz_scope=?, biz_area=?, company=?, flag=?, `time`=now(), file=?, certfi_no2=?, certfi_type=?, certfi_scope=?, certfi_valid_date=?, file2=? where id=?",
					m.get("name"),
					m.get("gender"),
					m.get("certNo"),
					m.get("certfiNo"),
					m.get("certfiStatus"),
					m.get("bizNo"),
					m.get("bizStatus"),
					m.get("validDate"),
					m.get("bizScope"),
					m.get("bizArea"),
					m.get("company"),
					flag,
					m.get("file"),
					m.get("certfiNo2"),
					m.get("certfiType"),
					m.get("certfiScope"),
					m.get("certfiValidDate"),
					m.get("file2"),
					id
			);
		}
	}
}
