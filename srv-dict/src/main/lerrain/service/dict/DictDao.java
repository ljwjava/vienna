package lerrain.service.dict;

import com.alibaba.fastjson.JSON;
import lerrain.tool.Common;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class DictDao
{
	@Autowired
	JdbcTemplate jdbc;

	public Object load(String company, String name, String version)
	{
		try
		{
			if ("0".equals(version) || "new".equalsIgnoreCase(version))
			{
				Map m = jdbc.queryForMap("select content, type from t_dict where code = ? and company = ? order by version desc limit 0, 1", name, company);
				return valOf(m);
			}
			else
			{
				Map m = jdbc.queryForMap("select content, type from t_dict where code = ? and company = ? and version = ?", name, company, version);
				return valOf(m);
			}
		}
		catch (Exception e)
		{
			return null;
		}
	}

	public Object load(String name, String version)
	{
		try
		{
			if ("0".equals(version) || "new".equalsIgnoreCase(version))
			{
				Map m = jdbc.queryForMap("select content, type from t_dict where code = ? and company is null order by version desc limit 0, 1", name);
				return valOf(m);
			}
			else
			{
				Map m = jdbc.queryForMap("select content, type from t_dict where code = ? and company is null and version = ?", name, version);
				return valOf(m);
			}
		}
		catch (Exception e)
		{
			return null;
		}
	}

	public List<Object> loadAll()
	{
		return jdbc.query("select content, type from t_dict", new RowMapper<Object>()
		{
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException
			{
				Map m = new HashMap();
				m.put("type", rs.getInt("type"));
				m.put("content", rs.getString("content"));

				return valOf(m);
			}
		});
	}

	private Object valOf(Map m)
	{
		int type = Common.intOf(m.get("type"), -1);
		String valstr = Common.trimStringOf(m.get("content"));

		Object val = null;
		if (type == 1)
			val = valstr;
		else if (type == 2)
			val = new BigDecimal(valstr);
		else if (type == 3)
			val = Integer.valueOf(valstr);
		else if (type == 4)
			val = JSON.parseObject(valstr);
		else if (type == 5)
			val = JSON.parseArray(valstr);
		else if (type == 7)
			val = "Y".equalsIgnoreCase(valstr);
		else if (type == 8)
			val = jdbc.queryForList(valstr);

		return val;
	}

}
