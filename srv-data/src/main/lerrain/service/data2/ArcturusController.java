package lerrain.service.data2;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lerrain.service.data2.source.SourceMgr;
import lerrain.service.data2.source.arcturus.ArcMap;
import lerrain.service.data2.source.arcturus.ArcTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

@Controller
public class ArcturusController
{
	@Autowired
	SourceMgr sourceMgr;

	@RequestMapping("/{db}/get.json")
	@ResponseBody
	public JSONObject get(@PathVariable String db, @RequestParam("id") Long id)
	{
		ArcMap lsm = (ArcMap)sourceMgr.getSource(db);

		JSONObject res = new JSONObject();
		res.put("result", "success");
		res.put("content", lsm.get(id));

		return res;
	}

	@RequestMapping("/{db}/put.json")
	@ResponseBody
	public JSONObject put(@PathVariable String db, @RequestBody JSON param)
	{
		ArcMap lsm = (ArcMap)sourceMgr.getSource(db);

		if (param instanceof JSONObject)
		{
			JSONObject js = (JSONObject)param;
			lsm.put(js.getLong("id"), js);
		}
		else
		{
			JSONArray list = (JSONArray)param;
			for (int i=0;i<list.size();i++)
			{
				JSONObject js = list.getJSONObject(i);
				lsm.put(js.getLong("id"), js);
			}
		}

		JSONObject res = new JSONObject();
		res.put("result", "success");

		return res;
	}

	@RequestMapping("/{db}/put/{id}")
	@ResponseBody
	public int put(@PathVariable String db, @PathVariable Long id, @RequestBody JSONObject param)
	{
		ArcMap lsm = (ArcMap)sourceMgr.getSource(db);
		lsm.put(id, param);

		return 1;
	}

	@RequestMapping("/{db}/find.json")
	@ResponseBody
	public JSONObject find(@PathVariable String db, HttpServletRequest req)
	{
		JSONObject res = new JSONObject();
		res.put("result", "success");

		ArcMap lsm = (ArcMap)sourceMgr.getSource(db);

		Enumeration<String> names = req.getParameterNames();
		if (names != null)
		{
			while (names.hasMoreElements())
			{
				String name = names.nextElement();
				res.put("content", ArcTool.find(lsm.getArcturus(), name, req.getParameter(name)));
			}
		}

		return res;
	}
}
