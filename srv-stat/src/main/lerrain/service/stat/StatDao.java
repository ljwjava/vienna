package lerrain.service.stat;

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
public class StatDao
{
	@Autowired
	JdbcTemplate jdbc;

	public void save(Map<String, Integer> map)
	{
		for (Map.Entry<String, Integer> e : map.entrySet())
		{
			String[] k = e.getKey().split(" / ");

			jdbc.update("replace into t_stat(`time`, platform_id, user_id, `action`, `count`) values(?, ?, ?, ?, ?)", k[0], k[1], k[2], k[3], e.getValue());
		}
	}
}
