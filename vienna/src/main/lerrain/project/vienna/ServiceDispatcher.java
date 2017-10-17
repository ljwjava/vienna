package lerrain.project.vienna;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lerrain.service.common.Log;
import lerrain.service.common.ServiceMgr;
import lerrain.tool.Common;
import lerrain.tool.Network;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Controller
public class ServiceDispatcher
{
//    @Value("${service.sale}")
//    private String srvUrl;

    @Autowired
    ServiceMgr sv;

    Map<String, String> modules = new HashMap<>();

    public ServiceDispatcher()
    {
        modules.put("ware", "sale");
        modules.put("sale", "sale");
        modules.put("printer", "printer");
        modules.put("dict", "dict");
        modules.put("order", "order");
    }

    private void verify(HttpSession session)
    {
        if (session.getAttribute("userId") == null)
            throw new RuntimeException("not login");
    }

//    @RequestMapping("/ware/perform.json")
//    @ResponseBody
//    @CrossOrigin
//    public JSONObject perform(@RequestBody JSONObject req)
//    {
//        try
//        {
//            String s1 = req.toJSONString();
//            String s2 = Network.request(srvUrl + "/perform.json", s1, 300000);
//            Log.debug(s1 + " => " + s2);
//
//            return JSONObject.parseObject(s2);
//        }
//        catch (Exception e)
//        {
//            throw new RuntimeException(e);
//        }
//    }

    @RequestMapping("/{module:ware|sale|dict|printer|order}/**")
    @ResponseBody
    @CrossOrigin
    public JSONObject redirect(HttpServletRequest req, @PathVariable String module, @RequestBody JSONObject param)
    {
        String uri = req.getRequestURI();
        return sv.req(modules.get(module), uri.substring(uri.indexOf("/", 1) + 1), param);
    }

    @RequestMapping("/ware/callback/**")
    @ResponseBody
    @CrossOrigin
    public String callback(HttpServletRequest req)
    {
        JSONObject param = null;

        String uri = req.getRequestURI();

        String contentType = req.getContentType();
        if (contentType != null)
            contentType = contentType.toLowerCase();

        Log.debug("CONTENT-TYPE: " + contentType);

        if (contentType != null && (contentType.indexOf("text") >= 0 || contentType.indexOf("json") >= 0))
        {
            try (InputStream is = req.getInputStream())
            {
                param = JSON.parseObject(Common.stringOf(is, "UTF-8"));
            }
            catch (Exception e)
            {
                Log.error(e);
            }
        }
        else
        {
            Enumeration<String> names = req.getParameterNames();
            if (names != null)
            {
                param = new JSONObject();
                while (names.hasMoreElements())
                {
                    String name = names.nextElement();
                    param.put(name, req.getParameter(name));
                }
            }
        }

        JSONObject res = sv.req(modules.get("ware"), uri.substring(uri.indexOf("/", 1) + 1), param);
        return res.getString("content");
    }

//    @RequestMapping("/{module}/{action}.{type:json|do}")
//    @ResponseBody
//    @CrossOrigin
//    public JSONObject redirect(HttpSession session, @PathVariable String module, @PathVariable String action, @PathVariable String type, @RequestBody JSONObject param)
//    {
//        if ("ware".equals(module) || "platform".equals(module))
//            return sv.req("sale", action + "." + type, param);
//        if ("dict".equals(module))
//            return sv.req("dict", action + "." + type, param);
//        if ("order".equals(module))
//            return sv.req("order", action + "." + type, param);
//        if ("printer".equals(module))
//            return sv.req("printer", action + "." + type, param);
//
//        verify(session);
//
//        param.put("owner", session.getAttribute("userId"));
//        param.put("platformId", session.getAttribute("platformId"));
//
//        if ("proposal".equals(module))
//            return sv.req("proposal", action + "." + type, param);
//        if ("customer".equals(module))
//            return sv.req("customer", action + "." + type, param);
//
//        throw new RuntimeException("no service");
//    }
//
//    @RequestMapping("/{module}/{sub}/{action}.{type:json|do}")
//    @ResponseBody
//    @CrossOrigin
//    public JSONObject redirect(HttpSession session, @PathVariable String module, @PathVariable String sub, @PathVariable String action, @PathVariable String type, @RequestBody JSONObject param)
//    {
//        if ("dict".equals(module))
//            return sv.req("dict", sub + "/" + action + "." + type, param);
//
//        verify(session);
//
//        param.put("owner", session.getAttribute("userId"));
//        param.put("platformId", session.getAttribute("platformId"));
//
//        if ("proposal".equals(module))
//            return sv.req("proposal", sub + "/" + action + "." + type, param);
//
//        JSONObject res = new JSONObject();
//        res.put("result", "fail");
//        res.put("reason", "no service");
//
//        return res;
//    }
}
