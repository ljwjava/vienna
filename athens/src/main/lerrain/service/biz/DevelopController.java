package lerrain.service.biz;

import com.alibaba.fastjson.JSONObject;
import lerrain.service.common.Log;
import lerrain.service.common.ServiceMgr;
import lerrain.service.env.EnvDao;
import lerrain.service.env.EnvService;
import lerrain.service.env.Environment;
import lerrain.tool.Common;
import lerrain.tool.formula.Factors;
import lerrain.tool.formula.Function;
import lerrain.tool.script.Script;
import lerrain.tool.script.Stack;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
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

    @Autowired
    AthensService athensSrv;

    @Autowired
    ServiceMgr serviceMgr;

    Map config = new HashMap();

    @RequestMapping("/admin/address")
    @ResponseBody
    public String service(@RequestBody JSONObject req)
    {
        serviceMgr.reset(req);
        return "success";
    }

    @RequestMapping({"/reset", "/admin/reset"})
    @ResponseBody
    public String reset()
    {
        athensSrv.reset();
        return "success";
    }

    @RequestMapping("/develop/list_gateway.json")
    @ResponseBody
    @CrossOrigin
    public JSONObject listGateway(@RequestBody JSONObject req)
    {
        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", developDao.loadGatewayList());

        return res;
    }

    @RequestMapping("/develop/list_env.json")
    @ResponseBody
    @CrossOrigin
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
    @CrossOrigin
    public JSONObject listFunction(@RequestBody JSONObject req)
    {
        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", developDao.loadFunctionList(req.getLong("envId")));

        return res;
    }

    @RequestMapping("/develop/function.json")
    @ResponseBody
    @CrossOrigin
    public JSONObject viewFunction(@RequestBody JSONObject req)
    {
        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", developDao.loadFunction(req.getLong("functionId")));

        return res;
    }

    @RequestMapping("/develop/test.json")
    @ResponseBody
    @CrossOrigin
    public JSONObject test(@RequestBody JSONObject req)
    {
        Long envId = req.getLong("envId");
        String script = req.getString("script");

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", Script.scriptOf(script).run(envSrv.getEnv(envId).getStack()));

        return res;
    }

    @RequestMapping("/develop/req_testing.json")
    @ResponseBody
    @CrossOrigin
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
    @CrossOrigin
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
    @CrossOrigin
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
            Function f = new EnvDao.InnerFunction(null, Script.scriptOf(script), Common.isEmpty(params) ? null : params.split(","), p.getStack());
            p.putVar(name, f);

        }

        JSONObject res = new JSONObject();
        res.put("result", "success");

        return res;
    }

    @RequestMapping("/develop/replace.json")
    @ResponseBody
    @CrossOrigin
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

            Function f = new EnvDao.InnerFunction(null, Script.scriptOf(script), Common.isEmpty(params) ? null : params.split(","), p.getStack());

            Log.debug("replace env's function... " + funcName);
            p.putVar(funcName, f);
        }

        JSONObject res = new JSONObject();
        res.put("result", "success");

        return res;
    }

    @RequestMapping("/develop/config.json")
    @ResponseBody
    @CrossOrigin
    public JSONObject config(@RequestBody JSONObject req)
    {
        config.putAll(req);

        JSONObject res = new JSONObject();
        res.put("result", "success");

        return res;
    }

    @RequestMapping("/develop/run.json")
    @ResponseBody
    @CrossOrigin
    public JSONObject runScript(HttpSession session, @RequestBody JSONObject req)
    {
        Long envId = req.getLong("envId");
        String script = req.getString("script");
        JSONObject self = req.getJSONObject("params");

        Stack stack = new Stack(envSrv.getEnv(envId).getStack());
        stack.declare("self", self);
        stack.declare("SESSION", new SessionAdapter(session));

        PrintStream oldPs = System.out;
        try (ByteArrayOutputStream sysOs = new ByteArrayOutputStream(); PrintStream sysPs = new PrintStream(sysOs))
        {
//            if (Common.boolOf(config.get("develop/log"), true))
//                System.setOut(sysPs);

            JSONObject res = new JSONObject();
            res.put("result", "success");

            JSONObject val = new JSONObject();
            try
            {
                val.put("result", Script.scriptOf(script).run(stack));
            }
            catch(Exception e)
            {
                try (ByteArrayOutputStream exOs = new ByteArrayOutputStream(); PrintStream exPs = new PrintStream(exOs))
                {
                    e.printStackTrace(exPs);
                    val.put("exception", exOs.toString());
                }
                catch (Exception e1)
                {
                    Log.error(e1);
                }
            }

            val.put("console", sysOs.toString());
            res.put("content", val);

            return res;
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
        finally
        {
            System.setOut(oldPs);
        }
    }
}
