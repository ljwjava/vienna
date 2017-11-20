package lerrain.service.biz;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lerrain.service.common.Log;
import lerrain.service.common.ServiceMgr;
import lerrain.tool.Common;
import lerrain.tool.script.Script;
import lerrain.tool.script.Stack;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Enumeration;
import java.util.Map;

@Controller
public class GatewayController
{
    @Autowired
    GatewayService gatewaySrv;

    @Autowired
    EnvService envSrv;

    @Autowired
    ServiceMgr sv;

    private Object call(String host, String uri, HttpSession session, JSONObject param)
    {
        Log.debug(host + "/" + uri + " <== " + param.toString());

        Gateway gateway = gatewaySrv.getGateway(uri);
        if (gateway == null)
            return null;

        if (gateway.isLogin())
        {
            Long userId = (Long)session.getAttribute("userId");
            if (userId == null)
                throw new RuntimeException("not login - " + uri);

            param.put("owner", userId);
            param.put("userId", userId);

            if (gateway.getWith() != null)
                for (String w : gateway.getWith())
                    param.put(w, session.getAttribute(w));
        }

        Object val = null;

        Script script = gateway.getScript();
        if (script != null)
        {
            Stack stack = new Stack(envSrv.getEnv(gateway.getEnvId()).getStack());
            stack.set("self", param);
            stack.set("SESSION", new SessionAdapter(session));

            val = script.run(stack);
        }

        Log.debug(uri + " ==> " + val);

        if (gateway.getForward() == Gateway.FORWARD_MICRO_SERVICE)
        {
            if (val instanceof Map)
                param.putAll((Map)val);

            String forwardTo = gateway.getForwardTo(uri);
            Log.debug("FORWARD: " + forwardTo);

            int p2 = forwardTo.indexOf("/");
            JSONObject json = sv.req(forwardTo.substring(0, p2), forwardTo.substring(p2 + 1), param);

            if (!"success".equals(json.getString("result")))
                throw new RuntimeException(json.getString("reason"));

            val = json.get("content");
        }

        return val;
    }

    private JSONObject getParam(HttpServletRequest req)
    {
        String contentType = req.getContentType();
        if (contentType != null)
            contentType = contentType.toLowerCase();

        JSONObject param = null;

        if (contentType != null && (contentType.indexOf("text") >= 0 || contentType.indexOf("json") >= 0))
        {
            try (InputStream is = req.getInputStream())
            {
                param = JSON.parseObject(Common.stringOf(is, "UTF-8"));
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            if (param == null)
                param = new JSONObject();
        }
        else
        {
            param = new JSONObject();
            Enumeration<String> names = req.getParameterNames();
            if (names != null)
            {
                while (names.hasMoreElements())
                {
                    String name = names.nextElement();
                    param.put(name, req.getParameter(name));
                }
            }
        }

        param.put("URL", req.getRequestURL().toString());

        return param;
    }

    @RequestMapping("/iyb/**/*.json")
    @ResponseBody
    @CrossOrigin
    public JSONObject iyb(HttpServletRequest req)
    {
        String uri = req.getRequestURI();
        uri = uri.substring(uri.indexOf("/", 1) + 1);

        JSONObject res = new JSONObject();
        try
        {
            JSONObject param = getParam(req);
            HttpSession session = req.getSession();

            Long userId = param.getLong("owner");
            if (userId == null)
                userId = param.getLong("accountId");
            if (userId != null)
                session.setAttribute("userId", userId);

            res.put("isSuccess", true);
            res.put("result", call(req.getServerName() + ":" + req.getServerPort(), uri, session, param));
        }
        catch (Exception e)
        {
            res.put("isSuccess", false);
            res.put("errorCode", 101);
            res.put("errorMsg", e.getMessage());
        }

        return res;
    }

    @RequestMapping("/test/**/*.json")
    @ResponseBody
    @CrossOrigin
    public JSONObject testJson(HttpServletRequest req)
    {
        String uri = req.getRequestURI();
        uri = uri.substring(6);

        JSONObject param = getParam(req);
        HttpSession session = req.getSession();

        PrintStream oldPs = System.out;
        try (ByteArrayOutputStream sysOs = new ByteArrayOutputStream(); PrintStream sysPs = new PrintStream(sysOs))
        {
            System.setOut(sysPs);

            JSONObject res = new JSONObject();
            res.put("result", "success");

            JSONObject val = new JSONObject();

            try
            {
                val.put("result", call(req.getServerName() + ":" + req.getServerPort(), uri, session, param));
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

    @RequestMapping("/api/**/*.json")
    @ResponseBody
    @CrossOrigin
    public JSONObject callJson(HttpServletRequest req)
    {
        String uri = req.getRequestURI();
        uri = uri.substring(5);

        long t1 = System.currentTimeMillis();
        Log.debug(uri + " => " + t1);

        JSONObject param = getParam(req);
        HttpSession session = req.getSession();

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", call(req.getServerName() + ":" + req.getServerPort(), uri, session, param));

        Log.debug(uri + " <= " + t1);

        return res;
    }

    @RequestMapping("/api/**/*.html")
    @CrossOrigin
    public void callHtml(HttpServletRequest req, HttpServletResponse res)
    {
        res.setContentType("text/html;charset=utf-8");

        try
        {
            String str = callAction(req);

            try (OutputStream os = res.getOutputStream())
            {
                os.write(str.getBytes("utf-8"));
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();

            try (OutputStream os = res.getOutputStream(); PrintStream ps = new PrintStream(os))
            {
                os.write("<pre>".getBytes());
                e.printStackTrace(ps);
                os.write("</pre>".getBytes());
            }
            catch (Exception e1)
            {
                e.printStackTrace();
            }
        }
    }

    @RequestMapping("/api/**/*.do")
    @CrossOrigin
    public String callAction(HttpServletRequest req)
    {
        String uri = req.getRequestURI();
        uri = uri.substring(5);

        JSONObject param = getParam(req);
        HttpSession session = req.getSession();

        return call(req.getServerName() + ":" + req.getServerPort(), uri, session, param).toString();
    }

}

