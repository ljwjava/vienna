package lerrain.service.customer;

import com.alibaba.fastjson.JSONObject;
import lerrain.tool.Common;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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
		String owner = json.getString("owner");

		String search = json.getString("search");
		int from = Common.intOf(json.get("from"), 0);
		int number = Common.intOf(json.get("number"), -1);

		JSONObject r = new JSONObject();
		r.put("list", cs.list(search, from, number, platformId, owner));
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
		String owner = json.getString("owner");

		String search = json.getString("search");
		int from = Common.intOf(json.get("from"), 0);
		int number = Common.intOf(json.get("number"), -1);

		JSONObject res = new JSONObject();
		res.put("result", "success");
		res.put("content", cs.count(search, platformId, owner));

		return res;
	}

	@RequestMapping("/view.json")
	@ResponseBody
	public JSONObject view(@RequestBody JSONObject json)
	{
		String customerId = json.getString("customerId");

		if (Common.isEmpty(customerId))
			throw new RuntimeException("no customerId");

		JSONObject res = new JSONObject();
		res.put("result", "success");
		res.put("content", cs.get(customerId));

		return res;
	}
	
	@RequestMapping("/save.json")
	@ResponseBody
	public JSONObject save(@RequestBody JSONObject p)
	{
		String customerId = cs.save(p);

		JSONObject res = new JSONObject();
		res.put("result", "success");
		res.put("content", customerId);

		return res;
	}
	
	@RequestMapping("/delete.json")
	@ResponseBody
	public JSONObject delete(@RequestBody JSONObject json)
	{
		String customerId = json.getString("customerId");

		if (!cs.delete(customerId))
			throw new RuntimeException("customerId<" + customerId + "> not found.");

		JSONObject res = new JSONObject();
		res.put("result", "success");

		return res;
	}
}
