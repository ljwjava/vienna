package lerrain.service.order;

import org.springframework.stereotype.Repository;

@Repository
public class OrderDaoMongo
{
//	@Autowired MongoTemplate mongo;
//
//	public void save(Order order)
//	{
//		Date now = new Date();
//
//		if (order.getCreateTime() == null)
//			order.setCreateTime(now);
//		order.setModifyTime(now);
//
//		mongo.save(order, "order");
//	}
//
//	public boolean exists(String orderId)
//	{
//		Query query = new Query();
//		query.addCriteria(Criteria.where("id").is(orderId));
//
//		return mongo.exists(query, "order");
//	}
//
//	public boolean delete(String orderId)
//	{
//		return true;
//	}
//
//	public Order load(String orderId)
//	{
//		Query query = new Query();
//		query.addCriteria(Criteria.where("id").is(orderId));
//
//		return mongo.findOne(query, Order.class, "order");
//	}
//
//	public List<Order> find(String channel)
//	{
//		Query query = new Query();
////		query.addCriteria(Criteria.where("platformId").is(platformId));
//		query.addCriteria(Criteria.where("platformCode").is(channel));
//
//		return mongo.find(query, Order.class, "order");
//	}
}
