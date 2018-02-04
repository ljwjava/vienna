package lerrain.service.customer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lerrain.service.common.ServiceTools;
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

	@Autowired
	ServiceTools tools;
	
	public List<Customer> list(String search, int from, int number, final Long platformId, final Long owner)
	{
		StringBuffer sql = new StringBuffer("select id,name,gender,birthday,city,mobile,email,type,cert_no,cert_type,owner,platform_id,create_time,update_time, null as detail from t_customer where platform_id = ? and owner = ? and valid is null");
		
		if (!Common.isEmpty(search))
			sql.append(" and name like '%" + search + "%' ");
		sql.append(" order by update_time desc limit " + from + ", " + number);
		
		return jdbc.query(sql.toString(), new Object[] {platformId, owner}, new RowMapper<Customer>()
		{
			@Override
			public Customer mapRow(ResultSet rs, int arg1) throws SQLException
			{
				return customerOf(rs);
			}
		});
	}

	public int count(String search, Long platformId, Long owner)
	{
		StringBuffer sql = new StringBuffer("select count(*) from t_customer where platform_id = ? and owner = ? and valid is null");

		if (!Common.isEmpty(search))
			sql.append(" and name like '%" + search + "%' ");

		return jdbc.queryForObject(sql.toString(), Integer.class, platformId, owner);
	}
	
	public boolean delete(Long customerId)
	{
		String sql = "update t_customer set valid = 'N', update_time = ? where id = ?";
		jdbc.update(sql, new Date(), customerId);
		
		return true;
	}
	
	public Customer load(Long customerId)
	{
		return jdbc.queryForObject("select * from t_customer where id = ?", new Object[] { customerId }, new RowMapper<Customer>()
		{
			@Override
			public Customer mapRow(ResultSet rs, int arg1) throws SQLException
			{
				return customerOf(rs);
			}
		});
	}

	public Long save(Customer c)
	{
		Date now = new Date();

		boolean isNew = true;
		if (c.getId() == null)
		{
			c.setId(tools.nextId("customer"));
		}
		else
		{
			Customer c1 = load(c.getId());
			if (c1 != null)
			{
				if (c1.getType() > c.getType())
					throw new RuntimeException("准客户不能覆盖真客户");

				isNew = false;
			}
		}
		
		if (isNew)
		{
			if (c.getOwner() == null || c.getPlatformId() == null)
				throw new RuntimeException("no owner or no platformId");

			String sql = "insert into t_customer (id, name, gender, birthday, cert_no, cert_type, city, mobile, email, type, detail, platform_id, owner, creator, updater, create_time, update_time) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			jdbc.update(sql, c.getId(), c.getName(), c.getGender(), c.getBirthday(), c.getCertNo(), c.getCertType(), c.get("city"), c.get("mobile"), c.get("email"), c.get("type"), JSON.toJSONString(c.getDetail()), c.getPlatformId(), c.getOwner(), c.getOwner(), c.getOwner(), now, now);
		}
		else
		{
			String sql = "update t_customer set name=?, gender=?, birthday=?, cert_no=?, cert_type=?, city=?, mobile=?, email=?, type=?, detail=?, updater=?, update_time=? where id=?";
			jdbc.update(sql, c.getName(), c.getGender(), c.getBirthday(), c.getCertNo(), c.getCertType(), c.get("city"), c.get("mobile"), c.get("email"), c.get("type"), JSON.toJSONString(c.getDetail()), c.getOwner(), now, c.getId());
		}

		return c.getId();
	}
	
	public Customer customerOf(ResultSet m) throws SQLException
	{
		Customer c = new Customer();
		c.setId(m.getLong("id"));
		c.setName(m.getString("name"));
		c.setGender(m.getString("gender"));
		c.setBirthday(m.getDate("birthday"));
		c.setType(Common.intOf(m.getString("type"), 0));
		c.setCertType(Common.intOf(m.getString("cert_type"), 0));
		c.setCertNo(m.getString("cert_no"));
		c.setPlatformId(m.getLong("platform_id"));
		c.setOwner(m.getLong("owner"));

		String str = m.getString("detail");
		JSONObject detail = str == null ? new JSONObject() : JSON.parseObject(str);
		detail.put("city", m.getString("city"));
		detail.put("email", m.getString("email"));
		detail.put("mobile", m.getString("mobile"));
		c.setDetail(detail);

		return c;
	}
}
