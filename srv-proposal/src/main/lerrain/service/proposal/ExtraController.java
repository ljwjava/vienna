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

	@RequestMapping("/list_clause.json")
	@ResponseBody
	public JSONObject listClause(@RequestBody JSONObject p)
	{
		JSONArray prds = new JSONArray();

		for (Product prd : ps.getClauses(p.getLong("platformId")))
		{
			if (prd.getType() != 2)
			{
				JSONObject item = new JSONObject();
				item.put("id", prd.getId());
				item.put("name", prd.getName());
				item.put("tag", prd.getTag());
				item.put("logo", prd.getLogo());
				item.put("remark", prd.getRemark());

				prds.add(item);
			}
		}

		JSONObject res = new JSONObject();
		res.put("result", "success");
		res.put("content", prds);

		return res;
	}

	@RequestMapping("/favourite.json")
	@ResponseBody
	public JSONObject favourite(@RequestBody JSONObject p)
	{
		String proposalId = p.getString("proposalId");

		if (Common.isEmpty(proposalId))
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
		String owner = p.getString("owner");
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
		String owner = p.getString("owner");
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
