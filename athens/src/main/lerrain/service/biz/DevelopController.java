package lerrain.service.biz;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lerrain.service.common.Log;
import lerrain.service.common.ServiceMgr;
import lerrain.service.env.EnvDao;
import lerrain.service.env.EnvService;
import lerrain.service.env.Environment;
import lerrain.service.env.KeyValService;
import lerrain.tool.Common;
import lerrain.tool.formula.Function;
import lerrain.tool.script.Script;
import lerrain.tool.script.ScriptRuntimeException;
import lerrain.tool.script.Stack;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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
    KeyValService keyValSrv;

    @Autowired
    DevelopDao developDao;

    @Autowired
    AthensService athensSrv;

    @Autowired
    ServiceMgr serviceMgr;

    Map config = new HashMap();

    Map<Long, JSONObject> temp = new HashMap();

    Long gatewayId;

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

    @RequestMapping("/admin/onclose")
    @ResponseBody
    public String onClose()
    {
        keyValSrv.store();
        return "success";
    }

    @RequestMapping("/admin/log/open")
    @ResponseBody
    @CrossOrigin
    public JSONObject logOpen(@RequestBody JSONObject req)
    {
        Script.STACK_MESSAGE = true;

        JSONObject res = new JSONObject();
        res.put("result", "success");

        return res;
    }

    @RequestMapping("/admin/log/fold")
    @ResponseBody
    @CrossOrigin
    public JSONObject logFold(@RequestBody JSONObject req)
    {
        Script.STACK_MESSAGE = false;

        JSONObject res = new JSONObject();
        res.put("result", "success");

        return res;
    }

    @RequestMapping("/develop/create_gateway.json")
    @ResponseBody
    @CrossOrigin
    public JSONObject createGateway(@RequestBody JSONObject req)
    {
        if (gatewayId == null)
            gatewayId = developDao.nextGatewayId();

        gatewayId++;

        JSONObject m = new JSONObject();
        if (req != null)
            m.putAll(req);
        m.put("id", gatewayId);

        temp.put(gatewayId, m);

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", gatewayId);

        return res;
    }

    @RequestMapping("/develop/list_gateway.json")
    @ResponseBody
    @CrossOrigin
    public JSONObject listGateway(@RequestBody JSONObject p)
    {
        int from = Common.intOf(p.get("from"), 0);
        int num = Common.intOf(p.get("num"), 20);

        JSONObject r = new JSONObject();
        r.put("list", developDao.listGateway(from, num));
        r.put("total", developDao.count());

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", r);

        return res;
    }

    @RequestMapping("/develop/view_gateway.json")
    @ResponseBody
    @CrossOrigin
    public JSONObject viewGateway(@RequestBody JSONObject req)
    {
        Long gatewayId = req.getLong("gatewayId");

        Map m;
        try
        {
            m = developDao.viewGateway(gatewayId);
        }
        catch (Exception e)
        {
            m = temp.get(gatewayId);
        }

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", m);

        return res;
    }

    @RequestMapping("/develop/query_gateway.json")
    @ResponseBody
    @CrossOrigin
    public JSONObject queryGateway(@RequestBody JSONObject req)
    {
        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", developDao.queryGateway());

        return res;
    }

    @RequestMapping("/develop/query_env.json")
    @ResponseBody
    @CrossOrigin
    public JSONObject listEnv()
    {
        List<Environment> list = envSrv.list();

        Map l = new LinkedHashMap<>();
        for (Environment env : list)
        {
            l.put(env.getId(), "" + env.getCode() + " / " + env.getName());
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

    @RequestMapping("/develop/query_function.json")
    @ResponseBody
    @CrossOrigin
    public JSONObject queryFunction(@RequestBody JSONObject req)
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
    public JSONObject test(HttpServletRequest reqs, @RequestBody JSONObject req)
    {
        Long envId = req.getLong("envId");
        String script = req.getString("script");

        JSONObject v = req.getJSONObject("param");
        Stack f = new Stack(envSrv.getEnv(envId).getStack());
        f.set("self", v);
        f.set("SESSION", new SessionAdapter(reqs.getSession()));

        PrintStream oldPs = System.out;
        try (ByteArrayOutputStream sysOs = new ByteArrayOutputStream(); PrintStream sysPs = new PrintStream(sysOs))
        {
            System.setOut(sysPs);

            JSONObject res = new JSONObject();
            res.put("result", "success");

            JSONObject val = new JSONObject();

            try
            {
                val.put("result", Script.scriptOf(script).run(f));
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

    @RequestMapping("/develop/load_cache.json")
    @ResponseBody
    @CrossOrigin
    public JSONObject reqParam(@RequestBody JSONObject req)
    {
        String url = req.getString("key");

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", JSON.parseObject(developDao.loadCache(url)));

        return res;
    }

    @RequestMapping("/develop/save_cache.json")
    @ResponseBody
    @CrossOrigin
    public JSONObject save(@RequestBody JSONObject req)
    {
        String url = req.getString("key");
        String param = req.getJSONObject("value").toJSONString();

        developDao.saveCache(url, param);

        JSONObject res = new JSONObject();
        res.put("result", "success");

        return res;
    }

    @RequestMapping("/develop/remove_cache.json")
    @ResponseBody
    @CrossOrigin
    public JSONObject remove(@RequestBody JSONObject req)
    {
        String url = req.getString("key");

        developDao.removeCache(url);

        JSONObject res = new JSONObject();
        res.put("result", "success");

        return res;
    }

    @RequestMapping("/develop/apply.json")
    @ResponseBody
    @CrossOrigin
    public JSONObject apply(@RequestBody JSONObject req)
    {
        try
        {
            Long gatewayId = req.getLong("gatewayId");
            Long envId = req.getLong("envId");
            int type = req.getIntValue("type");

            String forwardTo = Common.trimStringOf(req.getString("forward"));
            String script = req.getString("script");
            String with = Common.trimStringOf(req.getString("with"));
            String remark = Common.trimStringOf(req.getString("remark"));
            String seq = req.getString("sequence");

            String uri = Common.trimStringOf(req.getString("uri"));

            boolean login = Common.boolOf(req.getString("login"), true);

            if (Common.isEmpty(forwardTo))
                forwardTo = null;
            if (Common.isEmpty(with))
                with = null;

            if (gatewayId == null || envId == null)
                throw new RuntimeException("gatewayId or envId is null");

            developDao.save(gatewayId, envId, uri, type, forwardTo, script, login, with, Common.isEmpty(seq) ? null : Common.intOf(seq, 10000), remark);
            athensSrv.reset();
        }
        catch (Exception e)
        {
            e.printStackTrace();;
            throw e;
        }

        JSONObject res = new JSONObject();
        res.put("result", "success");

        return res;
    }

    @RequestMapping("/develop/apply_function.json")
    @ResponseBody
    @CrossOrigin
    public JSONObject applyFunction(@RequestBody JSONObject req)
    {
        try
        {
            int type = req.getIntValue("type");
            String script = req.getString("script");

            if (type == 2)
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
        }
        catch (Exception e)
        {
            e.printStackTrace();;
            throw e;
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
            catch (ScriptRuntimeException e)
            {
                val.put("exception", e.toStackString());
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
