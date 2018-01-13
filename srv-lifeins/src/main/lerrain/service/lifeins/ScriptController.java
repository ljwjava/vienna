package lerrain.service.lifeins;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lerrain.project.insurance.plan.Commodity;
import lerrain.project.insurance.plan.Plan;
import lerrain.project.insurance.product.*;
import lerrain.service.common.Log;
import lerrain.service.common.ServiceTools;
import lerrain.service.lifeins.manage.EditorService;
import lerrain.service.lifeins.quest.MergeQuestService;
import lerrain.tool.Common;
import lerrain.tool.script.Script;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ScriptController
{
    @Autowired
    PlanService planSrv;

    @Autowired
    ScriptService scriptSrv;

    Map<String, Script> scriptMap = new HashMap<>();

    @RequestMapping("/perform.json")
    @ResponseBody
    public JSONObject perform(@RequestBody JSONObject p)
    {
        Long scriptId = p.getLong("scriptId");
        JSONArray productId = p.getJSONArray("productId");
        String opt = p.getString("opt");

        Object r;
        if (scriptId != null || productId != null)
            r = scriptSrv.perform(scriptId, productId, Common.isEmpty(opt) ? null : opt, p.getJSONObject("with"));
        else
            throw new RuntimeException("缺少scriptId或productId");

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", r);

        return res;
    }

    @RequestMapping("/plan/perform.json")
    @ResponseBody
    public JSONObject planPerform(@RequestBody JSONObject p)
    {
        String planId = (String) p.get("planId");

        if (Common.isEmpty(planId))
            throw new RuntimeException("缺少planId");

        String str = p.getString("script");

        JSONObject res = new JSONObject();
        res.put("result", "success");

        if (Common.isEmpty(str))
        {
            res.put("content", planSrv.perform(planId, null, p.getJSONObject("with")));
        }
        else
        {
            Script script = scriptMap.get(str);
            if (script == null)
            {
                script = Script.scriptOf(str);
                scriptMap.put(str, script);
            }

            res.put("content", planSrv.perform(planId, script, p.getJSONObject("with")));
        }

//        queue.add(plan);

        return res;
    }

}
