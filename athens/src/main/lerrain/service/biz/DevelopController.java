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

    @RequestMapping("/develop/list_gateway.json")
    @ResponseBody
    public JSONObject listGateway(@RequestBody JSONObject req)
    {
        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", developDao.loadGatewayList());

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

    @RequestMapping("/develop/req_testing.json")
    @ResponseBody
    public JSONObject reqParam(@RequestBody JSONObject req)
    {
        String url = req.getString("url");

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", developDao.loadTesting(url));

        return res;
    }

    @RequestMapping("/develop/save.json")
    @ResponseBody
    public JSONObject save(@RequestBody JSONObject req)
    {
        String url = req.getString("url");
        String param = req.getString("param");

        developDao.save(url, param);

        JSONObject res = new JSONObject();
        res.put("result", "success");

        return res;
    }

    @RequestMapping("/develop/apply.json")
    @ResponseBody
    public JSONObject apply(@RequestBody JSONObject req)
    {
        int type = req.getIntValue("type");
        String script = req.getString("script");

        if (type == 1)
        {
            Long gatewayId = req.getLong("gatewayId");
            developDao.apply(gatewayId, script);

            Gateway p = gatewaySrv.getGateway(gatewayId);
            p.setScript(Script.scriptOf(script));
        }
        else if (type == 2)
        {
            Long functionId = req.getLong("functionId");
            String name = req.getString("name");
            String params = req.getString("params");

            developDao.apply(functionId, name, params, script);

            Long envId = req.getLong("envId");
            Environment p = envSrv.getEnv(envId);
            Function f = new EnvDao.InnerFunction(Script.scriptOf(script), Common.isEmpty(params) ? null : params.split(","), p.getStack());
            p.putVar(name, f);

        }

        JSONObject res = new JSONObject();
        res.put("result", "success");

        return res;
    }

    @RequestMapping("/develop/replace.json")
    @ResponseBody
    public JSONObject replace(@RequestBody JSONObject req)
    {
        int type = req.getIntValue("type");
        String script = req.getString("script");

        if (type == 1)
        {
            Long gatewayId = req.getLong("gatewayId");
            Gateway p = gatewaySrv.getGateway(gatewayId);

            Log.debug("replace gateway... " + p.getId());
            p.setScript(Script.scriptOf(script));
        }
        else
        {
            Long envId = req.getLong("envId");
            Environment p = envSrv.getEnv(envId);

            String funcName = req.getString("name");
            String params = req.getString("params");

            Function f = new EnvDao.InnerFunction(Script.scriptOf(script), Common.isEmpty(params) ? null : params.split(","), p.getStack());

            Log.debug("replace env's function... " + funcName);
            p.putVar(funcName, f);
        }

        JSONObject res = new JSONObject();
        res.put("result", "success");

        return res;
    }
}
