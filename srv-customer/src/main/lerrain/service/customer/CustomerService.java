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

	public int count(String search, Long platformId, String owner)
	{
		return customerDao.count(search, platformId, owner);
	}

	public List<Map> list(String search, Long platformId, String owner)
	{
		return list(search, 0, -1, platformId, owner);
	}
	
	public List<Map> list(String search, int from, int number, Long platformId, String owner)
	{
		return customerDao.list(search, from, number < 0 ? 999 : number, platformId, owner);
	}
	
	public Map get(String customerId)
	{
		return customerDao.load(customerId);
	}
	
	public String save(JSONObject c)
	{
		return customerDao.save(c);
	}
	
	public boolean delete(String customerId)
	{
		return customerDao.delete(customerId);
	}
}
