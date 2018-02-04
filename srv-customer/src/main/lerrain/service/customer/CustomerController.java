package lerrain.service.customer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lerrain.tool.Common;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class CustomerController
{
	@Autowired
	CustomerService cs;

	@RequestMapping("/list.json")
	@ResponseBody
	public JSONObject list(@RequestBody JSONObject json)
	{
		Long platformId = json.getLong("platformId");
		Long owner = json.getLong("owner");

		String search = json.getString("search");
		int from = Common.intOf(json.get("from"), 0);
		int number = Common.intOf(json.get("number"), -1);

		List<Customer> list = cs.list(search, from, number, platformId, owner);
		JSONArray ja = new JSONArray();
		for (Customer c : list)
			ja.add(CustomerTool.jsonOf(c));

		JSONObject r = new JSONObject();
		r.put("list", ja);
		r.put("total", cs.count(search, platformId, owner));

		JSONObject res = new JSONObject();
		res.put("result", "success");
		res.put("content", r);

		return res;
	}

	@RequestMapping("/query.json")
	@ResponseBody
	public JSONObject query(@RequestBody JSONObject json)
	{
		Long platformId = json.getLong("platformId");
		Long owner = json.getLong("owner");

		String search = json.getString("search");
		int from = Common.intOf(json.get("from"), 0);
		int number = Common.intOf(json.get("number"), -1);

		List<Customer> list = cs.list(search, from, number, platformId, owner);
		JSONArray ja = new JSONArray();
		for (Customer c : list)
			ja.add(CustomerTool.jsonOf(c));

		JSONObject res = new JSONObject();
		res.put("result", "success");
		res.put("content", ja);

		return res;
	}

	@RequestMapping("/view.json")
	@ResponseBody
	public JSONObject view(@RequestBody JSONObject json)
	{
		Long customerId = json.getLong("customerId");

		if (Common.isEmpty(customerId))
			throw new RuntimeException("no customerId");

		JSONObject res = new JSONObject();
		res.put("result", "success");
		res.put("content", CustomerTool.jsonOf(cs.get(customerId)));

		return res;
	}
	
	@RequestMapping("/save.json")
	@ResponseBody
	public JSONObject save(@RequestBody JSONObject m)
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
		JSONObject detail = JSON.parseObject(str);
		detail.put("city", m.getString("city"));
		detail.put("email", m.getString("email"));
		detail.put("mobile", m.getString("mobile"));
		c.setDetail(detail);

		Long customerId = cs.save(c);

		JSONObject res = new JSONObject();
		res.put("result", "success");
		res.put("content", customerId);

		return res;
	}
	
	@RequestMapping("/delete.json")
	@ResponseBody
	public JSONObject delete(@RequestBody JSONObject json)
	{
		Long customerId = json.getLong("customerId");

		if (!cs.delete(customerId))
			throw new RuntimeException("customerId<" + customerId + "> not found.");

		JSONObject res = new JSONObject();
		res.put("result", "success");

		return res;
	}
}
