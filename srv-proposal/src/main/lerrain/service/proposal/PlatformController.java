package lerrain.service.proposal;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lerrain.service.common.ServiceMgr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class PlatformController
{
	@Autowired
	ServiceMgr serviceMgr;

	@RequestMapping("/iyb/{path}")
	@ResponseBody
	@CrossOrigin
	public JSONObject iyb(@PathVariable String path, @RequestBody JSONObject p)
	{
		return t(serviceMgr.req("proposal", path, p));
	}

//	@RequestMapping("/iyb/plan/{path}.json")
//	@ResponseBody
//	@CrossOrigin
//	public JSONObject redirect(@PathVariable String path, @RequestBody JSONObject p)
//	{
//		return t(serviceMgr.req("lifeins", path, p));
//	}
//
//	@RequestMapping("iyb/bless/{srv}.json")
//	@ResponseBody
//	@CrossOrigin
//	public JSONObject bless(@PathVariable String path, @RequestBody JSONObject p)
//	{
//		return t(serviceMgr.req("proposal", path, p));
//	}

	private JSONObject t(JSONObject r)
	{
		JSONObject res = new JSONObject();
		if ("success".equals(r.get("result")))
		{
			res.put("isSuccess", true);
			res.put("result", r.get("content"));
		}
		else
		{
			res.put("errorCode", 101);
			res.put("errorMsg", r.get("reason"));
		}

		return res;
	}

	@RequestMapping("/list_adv.json")
	@ResponseBody
	@CrossOrigin
	public JSONObject listAdv(@RequestBody JSONObject p)
	{
		String owner = p.getString("owner");
		Long platformId = p.getLong("platformId");

		JSONArray c = new JSONArray();
		JSONObject j = new JSONObject();
		j.put("id", 1);
		j.put("url", "");
		j.put("type", "video");
		c.add(j);

		JSONObject res = new JSONObject();
		res.put("result", "success");
		res.put("content", c);

		return res;
	}
}
