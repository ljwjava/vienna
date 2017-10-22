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
public class DictDao
{
//	@Autowired
//	MongoTemplate mongo;
//
//	public Object load(String company, String name)
//	{
//		Query query = new Query();
//		query.addCriteria(Criteria.where("name").is(name).and("company").is(company));
//
//		Map map = mongo.findOne(query, Map.class, "dict");
//		if (map == null)
//			return null;
//
//		return map.get("content");
//	}

	@Autowired
	JdbcTemplate jdbc;

	public Object load(String company, String name, String version)
	{
		try
		{
			if ("0".equals(version) || "new".equalsIgnoreCase(version))
			{
				String c = jdbc.queryForObject("select content from t_dict where code = ? and company = ? order by version desc limit 0, 1", String.class, name, company);
				return JSON.parse(c);
			}
			else
			{
				String c = jdbc.queryForObject("select content from t_dict where code = ? and company = ? and version = ?", String.class, name, company, version);
				return JSON.parse(c);
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
				String c = jdbc.queryForObject("select content from t_dict where code = ? and company is null order by version desc limit 0, 1", String.class, name);
				return JSON.parse(c);
			}
			else
			{
				String c = jdbc.queryForObject("select content from t_dict where code = ? and company is null and version = ?", String.class, name, version);
				return JSON.parse(c);
			}
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
