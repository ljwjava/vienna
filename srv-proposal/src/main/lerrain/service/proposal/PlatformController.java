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
