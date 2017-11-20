package lerrain.service.biz;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lerrain.service.common.Log;
import lerrain.tool.Common;
import lerrain.tool.formula.Function;
import lerrain.tool.script.Script;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
public class DevelopController
{
    @Autowired
    EnvService envSrv;

    @Autowired
    GatewayService gatewaySrv;

    @Autowired
    DevelopDao developDao;

    @RequestMapping("/reset")
    @ResponseBody
    public String reset()
    {
        gatewaySrv.reset();
        envSrv.reset();

        return "success";
    }

    private Environment getEnv(JSONObject json)
    {
        Long envId = json.getLong("envId");
        if (envId != null)
            return envSrv.getEnv(envId);
        else
            return envSrv.getEnv(json.getString("envCode"));
    }

    @RequestMapping("/opt/gateway.json")
    @ResponseBody
    public JSONObject gateway(@RequestBody JSONObject req)
    {
        List<Gateway> list = gatewaySrv.getGatewayList("proposal");

        JSONArray r = new JSONArray();
        if (list != null) for (Gateway gateway : list)
        {
            JSONObject j = new JSONObject();
            j.put("uri", gateway.getUri());
            r.add(j);
        }

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", r);

        return res;
    }

    @RequestMapping("/develop/list_env.json")
    @ResponseBody
    public JSONObject listEnv()
    {
        List<Environment> list = envSrv.list();

        Map l = new LinkedHashMap<>();
        for (Environment env : list)
        {
            l.put(env.getId(), env.getCode());
        }

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", l);

        return res;
    }

    @RequestMapping("/develop/list_function.json")
    @ResponseBody
    public JSONObject listFunction(@RequestBody JSONObject req)
    {
        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", developDao.loadFunctionList(req.getLong("envId")));

        return res;
    }

    @RequestMapping("/develop/function.json")
    @ResponseBody
    public JSONObject viewFunction(@RequestBody JSONObject req)
    {
        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", developDao.loadFunction(req.getLong("functionId")));

        return res;
    }

    @RequestMapping("/develop/save.json")
    @ResponseBody
    public JSONObject save(@RequestBody JSONObject req)
    {
        Long functionId = req.getLong("functionId");
        String name = req.getString("name");
        String params = req.getString("params");
        String script = req.getString("script");
        String reqUrl = req.getString("url");
        String reqJson = req.getString("postJson");

        developDao.save(functionId, name, params, script, reqUrl, reqJson);

        JSONObject res = new JSONObject();
        res.put("result", "success");

        return res;
    }

    @RequestMapping("/develop/replace.json")
    @ResponseBody
    public JSONObject replace(@RequestBody JSONObject req)
    {
        String funcName = req.getString("name");
        String params = req.getString("params");
        String scriptStr = req.getString("script");

        Environment p = getEnv(req);
        Function f = new EnvDao.InnerFunction(Script.scriptOf(scriptStr), Common.isEmpty(params) ? null : params.split(","), p.getStack());

        Log.debug("replace env's function... " + funcName);
        p.putVar(funcName, f);

        JSONObject res = new JSONObject();
        res.put("result", "success");

        return res;
    }
}
