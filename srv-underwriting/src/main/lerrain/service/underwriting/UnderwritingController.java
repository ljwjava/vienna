package lerrain.service.underwriting;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lerrain.service.common.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
public class UnderwritingController
{
	@Autowired
	UnderwritingService uwSrv;

	@RequestMapping("/reset")
	@ResponseBody
	public String reset()
	{
		uwSrv.reset();
		return "success";
	}

	@RequestMapping("/create.json")
	@ResponseBody
	public JSONObject create(@RequestBody JSONObject p)
	{
		Underwriting uw = uwSrv.create(p.getJSONObject("answer"));

		JSONObject res = new JSONObject();
		res.put("result", "success");
		res.put("content", uw.getId());

		return res;
	}

	@RequestMapping("/query.json")
	@ResponseBody
	public JSONObject query(@RequestBody JSONObject p)
	{
		Log.info(p);

		Long uwId = p.getLong("uwId");

		String step = p.getString("step");
		JSONObject val = p.getJSONObject("answer");

		List<Quest> list = uwSrv.list(uwId, stepOf(step), val);

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
		j.put("feature", q.getFeature());
		j.put("text", q.getText());
		j.put("detail", q.getAnswer());
		j.put("disease", q.getDisease());
		j.put("sn", 1);

		if (q.getDisease() != null)
		{
			Quest d = uwSrv.getQuest(q.getDisease());
			if (d != null)
				j.put("diseaseName", d.getText());
		}

		return j;
	}

	@RequestMapping("/next.json")
	@ResponseBody
	public JSONObject next(@RequestBody JSONObject p)
	{
		Log.info(p);

		Long uwId = p.getLong("uwId");

		int step = stepOf(p.getString("step"));
		JSONObject val = p.getJSONObject("answer");

		if (val == null || val.isEmpty())
		{
			String ans = p.getString("reply");

			Underwriting uw = uwSrv.getUnderwriting(uwId);
			List<Quest> list = uw.getQuests(step);

			if (list != null) for (Quest q : list)
			{
				val = new JSONObject();
				val.put(q.getCode(), ans);
			}
		}

		char r = uwSrv.verify(uwId, step, val);

		JSONObject cnt = new JSONObject();
		if (r == Underwriting.RESULT_PASS)
		{
			Underwriting uw = uwSrv.getUnderwriting(uwId);
			cnt.put("result", "pass");
			cnt.put("applyUrl", uw.getAnswer().get("url"));
			cnt.put("notice", null);
		}
		else
		{
			cnt.put("result", r == Underwriting.RESULT_CONTINUE ? "next" : "fail");
			cnt.put("notice", null);
		}

		JSONObject res = new JSONObject();
		res.put("result", "success");
		res.put("content", cnt);

		Log.info(res);

		return res;
	}

	private int stepOf(String step)
	{
		if (step.equalsIgnoreCase("apply"))
			return Underwriting.STEP_APPLY;
		if (step.equalsIgnoreCase("health1"))
			return Underwriting.STEP_HEALTH1;
		if (step.equalsIgnoreCase("health2"))
			return Underwriting.STEP_HEALTH2;
		if (step.equalsIgnoreCase("disease"))
			return Underwriting.STEP_DISEASE;
		if (step.equalsIgnoreCase("disease1"))
			return Underwriting.STEP_DISEASE1;
		if (step.equalsIgnoreCase("disease2"))
			return Underwriting.STEP_DISEASE2;
		if (step.equalsIgnoreCase("disease3"))
			return Underwriting.STEP_DISEASE3;

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
		if (widget == Quest.WIDGET_TITLE)
			return "title";
		if (widget == Quest.WIDGET_TEXT)
			return "text";
		if (widget == Quest.WIDGET_SPACE)
			return "space";

		throw new RuntimeException("widget invalid");
	}

	@RequestMapping("/overall.json")
	@ResponseBody
	public JSONObject overall(@RequestBody JSONObject p)
	{
		JSONArray r = new JSONArray();

		Long uwId = p.getLong("uwId");
		Underwriting uw = uwSrv.getUnderwriting(uwId);

		Map<String, Object> ans = uw.getAnswer();

		for (int step : new int[] {Underwriting.STEP_APPLY, Underwriting.STEP_HEALTH1, Underwriting.STEP_HEALTH2, Underwriting.STEP_DISEASE1, Underwriting.STEP_DISEASE2})
		{
			List<Quest> list = uw.getQuests(step);
			if (list != null) for (Quest q : list)
			{
				JSONObject qj = new JSONObject();
				qj.put("quest", q.getCode());
				qj.put("answer", ans.get(q.getCode()));

				r.add(qj);
			}
		}

		JSONObject q = new JSONObject();
		q.put("insurant", r);

		JSONObject res = new JSONObject();
		res.put("result", "success");
		res.put("content", q);

		return res;
	}
}
