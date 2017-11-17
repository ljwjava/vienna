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
import javax.servlet.http.HttpSession;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Map;

@Controller
public class GatewayController
{
    @Autowired
    GatewayService gatewaySrv;

    @Autowired
    PlatformService platformSrv;

    @Autowired
    ServiceMgr sv;

    @PostConstruct
    @RequestMapping("/reset")
    @ResponseBody
    public String reset()
    {
        gatewaySrv.reset();
        platformSrv.reset();

        return "success";
    }

    private Object call(String host, String uri, HttpSession session, JSONObject param)
    {
        Log.debug(host + "/" + uri + " - " + session.getId());

        Gateway gateway = gatewaySrv.getGateway(host, uri);
        if (gateway == null)
            return null;

        param.put("platformId", gateway.getPlatformId());

        if (gateway.isLogin())
        {
//            Long userId = (Long)session.getAttribute("userId");
//            if (userId == null)
//                throw new RuntimeException("not login - " + uri);
//
//            Long platformIdSession = (Long)session.getAttribute("platformId");
//            if (platformIdSession != gateway.getPlatformId())
//                throw new RuntimeException("platform not match");
//
//            param.put("owner", userId);
//            param.put("userId", userId);

            if (gateway.getWith() != null)
                for (String w : gateway.getWith())
                    param.put(w, session.getAttribute(w));
        }

        Log.debug(param);

        Object val = null;

        Script script = gateway.getScript();
        if (script != null)
        {
            Stack stack = new Stack(platformSrv.getPlatform(gateway.getPlatformId()).getEnv());
            stack.set("self", param);
            stack.set("SESSION", new SessionAdapter(session));

            val = script.run(stack);
        }

        if (gateway.getForward() == Gateway.FORWARD_MICRO_SERVICE)
        {
            if (val instanceof Map)
                param.putAll((Map)val);

            String forwardTo = gateway.getForwardTo(uri);

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

        return param;
    }

    @RequestMapping("iyb/**/*.json")
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

            param.put("owner", param.get("accountId"));
            param.put("userId", param.get("accountId"));

            res.put("isSuccess", true);
            res.put("result", call(req.getServerName() + ":" + req.getServerPort(), uri, session, param));
        }
        catch (Exception e)
        {
            res.put("errorCode", 101);
            res.put("errorMsg", e.getMessage());
        }

        return res;
    }

    @RequestMapping("**/*.json")
    @ResponseBody
    @CrossOrigin
    public JSONObject callJson(HttpServletRequest req)
    {
        String uri = req.getRequestURI();
        if (uri.startsWith("/"))
            uri = uri.substring(1);

        JSONObject param = getParam(req);
        HttpSession session = req.getSession();

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", call(req.getServerName() + ":" + req.getServerPort(), uri, session, param));

        return res;
    }

    @RequestMapping("**/*.html")
    @ResponseBody
    @CrossOrigin
    public String callHtml(HttpServletRequest req)
    {
        return callAction(req);
    }

    @RequestMapping("**/*.do")
    @CrossOrigin
    public String callAction(HttpServletRequest req)
    {
        String uri = req.getRequestURI();
        if (uri.startsWith("/"))
            uri = uri.substring(1);

        JSONObject param = getParam(req);
        HttpSession session = req.getSession();

        return call(req.getServerName() + ":" + req.getServerPort(), uri, session, param).toString();
    }

}

