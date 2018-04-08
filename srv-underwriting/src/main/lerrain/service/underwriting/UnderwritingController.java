package lerrain.service.underwriting;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class UnderwritingController
{
	@Autowired
	UnderwritingService uwSrv;

	@RequestMapping("/create.json")
	@ResponseBody
	public JSONObject create(@RequestBody JSONObject p)
	{
		Underwriting uw = uwSrv.create();

		JSONObject res = new JSONObject();
		res.put("result", "success");
		res.put("content", uw.getId());

		return res;
	}

	@RequestMapping("/query.json")
	@ResponseBody
	public JSONObject query(@RequestBody JSONObject p)
	{
		Long uwId = p.getLong("uwId");

		String step = p.getString("step");
		JSONObject val = p.getJSONObject("answer");
		JSONArray disease = p.getJSONArray("disease");

		List<Quest> list = uwSrv.find(uwId, stepOf(step), val);

		JSONArray r = new JSONArray();
		for (Quest q : list)
			r.add(jsonOf(q));

		JSONObject res = new JSONObject();
		res.put("result", "success");
		res.put("content", r);

		return res;
	}

	private JSONObject jsonOf(Quest q)
	{
		JSONObject j = new JSONObject();
		j.put("code", q.getCode());
		j.put("widget", widgetOf(q.getWidget()));
		j.put("text", q.getText());
		j.put("detail", q.getAnswer());
		j.put("sn", 1);

		return j;
	}

	@RequestMapping("/next.json")
	@ResponseBody
	public JSONObject next(@RequestBody JSONObject p)
	{
		Long uwId = p.getLong("uwId");

		String step = p.getString("step");
		JSONObject val = p.getJSONObject("answer");

		char r = uwSrv.verify(uwId, stepOf(step), val);

		JSONObject cnt = new JSONObject();
		cnt.put("result", r == Underwriting.RESULT_PASS ? "pass" : r == Underwriting.RESULT_CONTINUE ? "next" : "fail");
		cnt.put("notice", null);

		JSONObject res = new JSONObject();
		res.put("result", "success");
		res.put("content", cnt);

		return res;
	}

	private int stepOf(String step)
	{
		if (step == "apply")
			return Underwriting.STEP_APPLY;

		throw new RuntimeException("step invalid");
	}

	private String widgetOf(int widget)
	{
		if (widget == Quest.WIDGET_SWITCH)
			return "switch";
		if (widget == Quest.WIDGET_SELECT)
			return "select";
		if (widget == Quest.WIDGET_INPUT)
			return "input";

		throw new RuntimeException("widget invalid");
	}
}
