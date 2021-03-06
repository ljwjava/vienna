package lerrain.service.proposal;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lerrain.service.common.ServiceMgr;
import lerrain.tool.Common;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class ExtraController
{
	@Autowired
	ProposalService ps;

	@RequestMapping("/cover.json")
	@ResponseBody
	@CrossOrigin
	public JSONObject cover(@RequestBody JSONObject p)
	{
		JSONObject res = new JSONObject();
		res.put("result", "success");
		res.put("content", ps.getCovers());

		return res;
	}

	@RequestMapping("/favourite.json")
	@ResponseBody
	public JSONObject favourite(@RequestBody JSONObject p)
	{
		Long proposalId = p.getLong("proposalId");

		if (proposalId == null)
			throw new RuntimeException("缺少proposalId");

		boolean b = Common.boolOf(p.get("favourite"), false);
		ps.setFavourite(proposalId, b);

		JSONObject res = new JSONObject();
		res.put("result", "success");

		return res;
	}

	@RequestMapping("/bless/list.json")
	@ResponseBody
	public JSONObject listBless(@RequestBody JSONObject p)
	{
		Long owner = p.getLong("owner");
		Long platformId = p.getLong("platformId");

		JSONArray c = new JSONArray();
		List<Object[]> r = ps.listBless(owner, platformId);
		if (r != null) for (Object[] l : r)
		{
			JSONObject j = new JSONObject();
			j.put("id", l[0]);
			j.put("text", l[1]);
			c.add(j);
		}

		JSONObject res = new JSONObject();
		res.put("result", "success");
		res.put("content", c);

		return res;
	}

	@RequestMapping("/bless/save.json")
	@ResponseBody
	public JSONObject saveBless(@RequestBody JSONObject p)
	{
		Long owner = p.getLong("owner");
		Long platformId = p.getLong("platformId");
		Long blessId = p.getLong("blessId");
		String text = p.getString("text");

		ps.saveBless(blessId, text, owner, platformId);

		JSONObject res = new JSONObject();
		res.put("result", "success");

		return res;
	}

	@RequestMapping("/bless/delete.json")
	@ResponseBody
	public JSONObject deleteBless(@RequestBody JSONObject p)
	{
		Long blessId = p.getLong("blessId");
		ps.deleteBless(blessId);

		JSONObject res = new JSONObject();
		res.put("result", "success");

		return res;
	}

	@RequestMapping("/list_adv.json")
	@ResponseBody
	public JSONObject listAdv(@RequestBody JSONObject p)
	{
		Long owner = p.getLong("owner");
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
