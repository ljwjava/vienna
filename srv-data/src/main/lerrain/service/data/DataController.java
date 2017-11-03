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

	Map map = new HashMap();

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
	public JSONObject run(@PathVariable Long optId, @RequestBody(required = false) JSONObject param)
	{
		JSONObject res = new JSONObject();
		res.put("result", "success");
		res.put("content", taskService.run(optId, param));

		return res;
	}
}
