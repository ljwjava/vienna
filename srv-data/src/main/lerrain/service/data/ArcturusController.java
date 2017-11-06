package lerrain.service.data;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lerrain.service.common.Log;
import lerrain.service.data.source.SourceMgr;
import lerrain.service.data.source.arcturus.ArcMap;
import lerrain.tool.script.Stack;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PostConstruct;

@Controller
public class ArcturusController
{
	@Autowired
	SourceMgr sourceMgr;

	ArcMap lsm;

	@RequestMapping("/user.json")
	@ResponseBody
	public JSONObject user(@RequestBody JSONObject param)
	{
		if (lsm == null)
			lsm = (ArcMap)sourceMgr.getSource(2L);

		Long key = param.getLong("id");

		JSONObject res = new JSONObject();
		res.put("result", "success");
		res.put("content", lsm.get(key));

		return res;
	}

	@RequestMapping("/user/{key}")
	@ResponseBody
	public String save(@PathVariable Long key, @RequestBody JSONObject param)
	{
		if (lsm == null)
			lsm = (ArcMap)sourceMgr.getSource(2L);

		lsm.put(key, param);

		return "success";
	}

	@RequestMapping("/user/save")
	@ResponseBody
	public String saveAll(@RequestBody JSONArray list)
	{
		if (lsm == null)
			lsm = (ArcMap)sourceMgr.getSource(2L);

		for (int i=0;i<list.size();i++)
		{
			JSONObject param = list.getJSONObject(i);
			lsm.put(param.getLong("id"), param);
		}

		return "success";
	}

	@RequestMapping("/user/iterator")
	@ResponseBody
	public JSONObject iterator()
	{
		JSONArray j = new JSONArray();

		long t1 = System.currentTimeMillis();

		for (Long k : lsm.keySet())
			j.add(new Object[] {k, lsm.get(k)});

		long t2 = System.currentTimeMillis() - t1;

		JSONObject res = new JSONObject();
		res.put("result", "success");
		res.put("content", new Object[] { t2, j.toString() });

		return res;
	}
}
