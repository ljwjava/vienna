package lerrain.service.customer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lerrain.tool.Common;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
public class CustomerDao
{
	@Autowired
	JdbcTemplate jdbc;
	
	public List<Map> list(String search, int from, int number, final Long platformId, final String owner)
	{
		StringBuffer sql = new StringBuffer("select customer_id,name,gender,birthday,city,mobile,type,owner,platform_id,create_time,update_time, null as detail from t_customer where platform_id = ? and owner = ? and valid is null");
		
		if (!Common.isEmpty(search))
			sql.append(" and name like '%" + search + "%' ");
		sql.append(" order by update_time desc limit " + from + ", " + number);
		
		return jdbc.query(sql.toString(), new Object[] {platformId, owner}, new RowMapper<Map>()
		{
			@Override
			public Map mapRow(ResultSet rs, int arg1) throws SQLException
			{
				return customerOf(rs);
			}
		});
	}

	public int count(String search, Long platformId, String owner)
	{
		StringBuffer sql = new StringBuffer("select count(*) from t_customer where platform_id = ? and owner = ? and valid is null");

		if (!Common.isEmpty(search))
			sql.append(" and name like '%" + search + "%' ");

		return jdbc.queryForObject(sql.toString(), Integer.class, platformId, owner);
	}
	
	public boolean delete(String customerId)
	{
		String sql = "update t_customer set valid = 'N', update_time = ? where customer_id = ?";
		jdbc.update(sql, new Date(), customerId);
		
		return true;
	}
	
	public JSONObject load(String customerId)
	{
		StringBuffer sql = new StringBuffer("select customer_id,name,gender,birthday,city,mobile,type,owner,platform_id,create_time,update_time,detail from t_customer where customer_id = ?");
		return jdbc.queryForObject(sql.toString(), new Object[]{customerId}, new RowMapper<JSONObject>()
		{
			@Override
			public JSONObject mapRow(ResultSet rs, int arg1) throws SQLException
			{
				return customerOf(rs);
			}
		});
	}

	public String save(JSONObject c)
	{
		Date now = new Date();

		boolean isNew = true;
		String customerId = c.getString("customerId");
		String owner = c.getString("owner");

		if (Common.isEmpty(customerId))
		{
			customerId = Common.nextId("customer");
		}
		else
		{
			JSONObject map = load(customerId);
			if (map != null)
			{
				int type1 = Common.intOf(map.get("type"), 1);
				int type2 = Common.intOf(c.get("type"), 1);

				if (type1 > type2)
					throw new RuntimeException("准客户不能覆盖真客户");

				map.putAll(c);
				c = map;

				isNew = false;
			}
		}
		
		if (isNew)
		{
			Long platformId = Common.toLong(c.get("platformId"));
			if (owner == null || platformId == null)
				throw new RuntimeException("no owner or no platformId");

			String sql = "insert into t_customer (customer_id, name, gender, birthday, city, mobile, type, detail, platform_id, owner, creator, updater, create_time, update_time) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			jdbc.update(sql, customerId, c.get("name"), c.get("gender"), c.get("birthday"), c.get("city"), c.get("mobile"), c.get("type"), JSON.toJSON(c).toString(), platformId, owner, owner, owner, now, now);
		}
		else
		{
			String sql = "update t_customer set name=?, gender=?, birthday=?, city=?, mobile=?, type=?, detail=?, updater=?, update_time=? where customer_id=?";
			jdbc.update(sql, c.get("name"), c.get("gender"), c.get("birthday"), c.get("city"), c.get("mobile"), c.get("type"), JSON.toJSON(c).toString(), owner, now);
		}

		return customerId;
	}
	
	public JSONObject customerOf(ResultSet m) throws SQLException
	{
		String str = m.getString("detail");
		JSONObject c = Common.isEmpty(str) ? new JSONObject() : JSON.parseObject(str);

		c.put("customerId", m.getString("customer_id"));
		c.put("name", m.getString("name"));
		c.put("birthday", m.getDate("birthday"));
		c.put("gender", m.getString("gender"));
		c.put("city", m.getString("city"));
		c.put("mobile", m.getString("mobile"));
		c.put("type", Common.intOf(m.getString("type"), 1));
		c.put("platformId", m.getLong("platform_id"));
		c.put("owner", m.getString("owner"));
		c.put("createTime", m.getDate("create_time"));
		c.put("updateTime", m.getDate("update_time"));

		return c;
	}
}
