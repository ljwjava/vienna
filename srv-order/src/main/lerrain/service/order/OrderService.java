package lerrain.service.order;

import lerrain.service.common.ServiceTools;
import lerrain.tool.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class OrderService
{
	@Autowired
	ServiceTools tools;

	@Autowired
	OrderDaoJdbc pd;

	Cache cache = new Cache();

	public Order newOrder()
	{
		Order order = new Order();
		order.setId(tools.nextId("order"));
		order.setCreateTime(new Date());

		cache.put(order.getId(), order);
		
		return order;
	}

	public void setChildren(Order order, List<Order> children)
	{
		List<Long> list = new ArrayList<>();

		for (Order c : children)
		{
			c.setParentId(order.getId());
			list.add(c.getId());
		}

		order.setChildren(list);
		pd.saveChildren(order, children);
	}
	
	public Order getOrder(Long orderId)
	{
		Order order = cache.get(orderId);

		if (order == null)
		{
			order = pd.load(orderId);
			cache.put(order.getId(), order);
		}
		
		return order;
	}

	public Order reloadOrder(Long orderId)
	{
		Order order = pd.load(orderId);
		cache.put(order.getId(), order);

		return order;
	}
	
	public void saveOrder(Order p)
	{
		pd.save(p);
	}

	public void update(Order p)
	{
		p.setModifyTime(new Date());
		pd.update(p);
	}

	public List<Order> list(int type, int from, int number, Long platformId, Long owner)
	{
		return pd.list(type, from, number, platformId, owner);
	}

	public int count(int type, Long platformId, Long owner)
	{
		return pd.count(type, platformId, owner);
	}
}
