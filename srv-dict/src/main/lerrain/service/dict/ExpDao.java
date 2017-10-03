package lerrain.service.dict;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class ExpDao
{
	@Autowired
	JdbcTemplate jdbc;

	public synchronized Object load(String company, String name)
	{
		try
		{
			String c = jdbc.queryForObject("select content from t_dict where code = ? and company = ? order by version desc", String.class, name, company);
			return JSON.parse(c);
		}
		catch (Exception e)
		{
			return null;
		}
	}

	public Object load(String name)
	{
		try
		{
			String c = jdbc.queryForObject("select content from t_dict where code = ? and company is null order by version desc", String.class, name);
			return JSON.parse(c);
		}
		catch (Exception e)
		{
			return null;
		}
	}

	public List<Object> loadAll()
	{
		return jdbc.query("select content from t_dict", new RowMapper<Object>()
		{
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException
			{
				return JSON.parse(rs.getString("content"));
			}
		});
	}
}
