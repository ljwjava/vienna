package lerrain.service.customer;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomerService
{
	@Autowired
	CustomerDao customerDao;

	public int count(String search, Long platformId, Long owner)
	{
		return customerDao.count(search, platformId, owner);
	}

	public List<Customer> list(String search, Long platformId, Long owner)
	{
		return list(search, 0, -1, platformId, owner);
	}
	
	public List<Customer> list(String search, int from, int number, Long platformId, Long owner)
	{
		return customerDao.list(search, from, number < 0 ? 999 : number, platformId, owner);
	}
	
	public Customer get(Long customerId)
	{
		return customerDao.load(customerId);
	}
	
	public Long save(Customer c)
	{
		return customerDao.save(c);
	}
	
	public boolean delete(Long customerId)
	{
		return customerDao.delete(customerId);
	}
}
