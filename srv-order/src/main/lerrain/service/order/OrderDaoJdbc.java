package lerrain.service.order;

import com.alibaba.fastjson.JSON;
import lerrain.tool.Common;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

@Repository
public class OrderDaoJdbc
{
	@Autowired
	JdbcTemplate jdbc;

	public void save(Order order)
	{
		Date now = new Date();

		if (order.getCreateTime() == null)
			order.setCreateTime(now);
		order.setModifyTime(now);

		if (!exists(order.getId()))
		{
			jdbc.update("insert into t_order(id,parent_id,apply_no,biz_no,product_id,product_type,product_name,vendor_id,platform_id,owner,price,pay,type,status,detail,extra,create_time,creator,update_time,updater) " +
					"values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
				order.getId(),
				order.getParentId(),
				order.getApplyNo(),
				order.getBizNo(),
				order.getProductId(),
				order.getProductType(),
				order.getProductName(),
				order.getVendorId(),
				order.getPlatformId(),
				order.getOwner(),
				order.getPrice(),
				order.getPay(),
				order.getType(),
				order.getStatus(),
				order.getDetail() == null ? null : JSON.toJSONString(order.getDetail()),
				order.getExtra() == null ? null : JSON.toJSONString(order.getExtra()),
				order.getCreateTime(),
				order.getOwner(),
				order.getModifyTime(),
				order.getOwner()
			);
		}
		else
		{
			jdbc.update("update t_order set apply_no=?,biz_no=?, type=?, product_id=?, product_type=?, product_name=?, vendor_id=?, price=?, pay=?, status=?, detail=?, extra=?, update_time=? where id=?", order.getApplyNo(), order.getBizNo(), order.getType(), order.getProductId(), order.getProductType(), order.getProductName(), order.getVendorId(), order.getPrice(), order.getPay(), order.getDetail() == null ? null : order.getStatus(), order.getDetail() != null ? JSON.toJSONString(order.getDetail()) : null, order.getExtra() != null ? JSON.toJSONString(order.getExtra()) : null, order.getModifyTime(), order.getId());
		}
	}

	public void update(Order order)
	{
		jdbc.update("update t_order set apply_no=?, biz_no=?, pay=?, status=?, extra=?, update_time=? where id=?", order.getApplyNo(), order.getBizNo(), order.getPay(), order.getStatus(), order.getExtra() != null ? JSON.toJSONString(order.getExtra()) : null, order.getModifyTime(), order.getId());
	}

	public boolean exists(Long orderId)
	{
		return jdbc.queryForObject("select exists(select id from t_order where id = ?) from dual", Integer.class, orderId) > 0;
	}

	public void saveChildren(Order order, List<Order> children)
	{
		jdbc.update("update t_order set valid = 'N' where parent_id = ?", order.getId());

		for (Order child : children)
			save(child);
	}

	public boolean delete(Long orderId)
	{
		return jdbc.update("update t_order set valid = 'N' where id = ?", orderId) > 0;
	}
	
	public Order load(Long orderId)
	{
		Order order = jdbc.queryForObject("select * from t_order where id = ?", new Object[]{orderId}, new RowMapper<Order>()
		{
			@Override
			public Order mapRow(ResultSet rs, int rowNum) throws SQLException
			{
				return orderOf(rs);
			}

		});

		order.setChildren(jdbc.queryForList("select id from t_order where parent_id = ?", new Object[]{orderId}, Long.class));
		return order;
	}

	public List<Order> query(int type, Long owner, int from, int number)
	{
		return jdbc.query("select * from t_order where valid is null and parent_id is null and type = ? and owner = ? order by create_time desc limit ?, ?", new Object[]{type, owner, from, number}, new RowMapper<Order>()
		{
			@Override
			public Order mapRow(ResultSet m, int rowNum) throws SQLException
			{
				Order order = new Order();
				order.setId(m.getLong("id"));
				order.setApplyNo(m.getString("apply_no"));
				order.setBizNo(m.getString("biz_no"));
				order.setType(m.getInt("type"));
				order.setProductId(m.getString("product_id"));
				order.setProductType(m.getInt("product_type"));
				order.setProductName(m.getString("product_name"));
				order.setVendorId(m.getLong("vendor_id"));
				order.setCreateTime(m.getTimestamp("create_time"));
				order.setModifyTime(m.getTimestamp("update_time"));
				order.setPlatformId(m.getLong("platform_id"));
				order.setOwner(m.getString("owner"));
				order.setPrice(m.getBigDecimal("price"));
				order.setPay(m.getInt("pay"));
				order.setStatus(m.getInt("status"));

				return order;
			}
		});
	}
	
	private Order orderOf(ResultSet m) throws SQLException
	{
		Order order = new Order();
		order.setId(m.getLong("id"));
		order.setApplyNo(m.getString("apply_no"));
		order.setBizNo(m.getString("biz_no"));
		order.setType(m.getInt("type"));
		order.setProductId(m.getString("product_id"));
		order.setProductType(m.getInt("product_type"));
		order.setProductName(m.getString("product_name"));
		order.setVendorId(m.getLong("vendor_id"));
		order.setCreateTime(m.getTimestamp("create_time"));
		order.setModifyTime(m.getTimestamp("update_time"));
		order.setPlatformId(m.getLong("platform_id"));
		order.setOwner(m.getString("owner"));
		order.setPrice(m.getBigDecimal("price"));
		order.setPay(m.getInt("pay"));
		order.setStatus(m.getInt("status"));

		String detail = m.getString("detail");
		if (!Common.isEmpty(detail))
			order.setDetail(JSON.parseObject(detail));
		String extra = m.getString("extra");
		if (!Common.isEmpty(extra))
			order.setExtra(JSON.parseObject(extra));

		return order;
	}
}
