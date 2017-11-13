package lerrain.service.sale;

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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Map;

public class GatewayController
{
    @Autowired
    GatewayService gatewaySrv;

    @Autowired
    ServiceMgr sv;

    private String call(HttpServletRequest req)
    {
        String uri = req.getRequestURI();

        Gateway gateway = gatewaySrv.getGateway(req.getServerName() + ":" + req.getServerPort(), uri);

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
                Log.error(e);
                param = new JSONObject();
            }
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

        Long platformId = gatewaySrv.getPlatformId(req.getServerName() + ":" + req.getServerPort());
        param.put("platformId", platformId);

        if (gateway.isLogin())
        {
            HttpSession session = req.getSession();

            Long userId = (Long)session.getAttribute("userId");
            if (userId == null)
                throw new RuntimeException("not login");

            Long platformIdSession = (Long)session.getAttribute("platformId");
            if (platformIdSession != platformId)
                throw new RuntimeException("platform not match");

            if (gateway.getWith() != null)
                for (String w : gateway.getWith())
                    param.put(w, session.getAttribute(w));
        }

        Object val = null;

        Script script = gateway.getScript();
        if (script != null)
        {
            Stack stack = new Stack(param);
            val = script.run(stack);
        }

        String redirect = gateway.getRedirect();
        if (redirect != null)
        {
            if (val != null)
                param.putAll((Map)val);

            int p2 = redirect.indexOf("/", 1);
            return sv.reqStr(uri.substring(0, p2), uri.substring(p2 + 1), param);

//            if (!"success".equals(json.getString("result")))
//                throw new RuntimeException(json.getString("reason"));
//
//            val = json.get("content");
        }

        return val.toString();
    }


    @RequestMapping({ "/**.json" })
    @ResponseBody
    public String callJson(HttpServletRequest req)
    {
        return call(req);
    }

    @RequestMapping("/**.html")
    @ResponseBody
    @CrossOrigin
    public String callHtml(HttpServletRequest req)
    {
        return (String)call(req);
    }

    @RequestMapping("/**.do")
    @CrossOrigin
    public String callAction(HttpServletRequest req)
    {
        return (String)call(req);
    }

}
