package lerrain.service.data;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lerrain.tool.script.Stack;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@Controller
public class DataController
{
	@Autowired DataService service;
	@Autowired TaskService taskService;
	@Autowired ModelService modelService;

	@PostConstruct
	@RequestMapping("/reset")
	@ResponseBody
	@CrossOrigin
	public String reset()
	{
		service.reset();
		taskService.reset();

		modelService.reset(service.envMap);

		return "success";
	}

	@RequestMapping({"/run/{optId}"})
	@ResponseBody
	public String run(@PathVariable Long optId, @RequestBody(required = false) JSONObject param)
	{
		long t1 = System.currentTimeMillis();
		return "result: " + taskService.run(optId, param) + ", in " + (System.currentTimeMillis() - t1) / 1000.0f + "s";
	}

	@RequestMapping({"/run.json"})
	@ResponseBody
	public JSONObject runTask( @RequestBody JSONObject param)
	{
		Long optId = param.getLong("id");

		long t1 = System.currentTimeMillis();
		Object result = taskService.run(optId, param);

		JSONObject res = new JSONObject();
		res.put("result", "success");
		res.put("content", result);
		res.put("time", System.currentTimeMillis() - t1);

		return res;
	}
}
