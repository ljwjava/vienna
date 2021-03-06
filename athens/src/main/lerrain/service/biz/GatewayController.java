package lerrain.service.biz;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lerrain.service.common.Log;
import lerrain.service.common.ServiceMgr;
import lerrain.tool.Common;
import lerrain.tool.Disk;
import lerrain.tool.script.Script;
import lerrain.tool.script.ScriptRuntimeException;
import lerrain.tool.script.Stack;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * Annotate lyx
 */
@Controller
@ControllerAdvice
public class GatewayController
{
    @Autowired
    GatewayService gatewaySrv;

    @Autowired
    ServiceMgr sv;

    @Value("${gatedir}")
    String gateDir;

    @Value("${path.temp}")
    String tempDir;

    @ExceptionHandler(ScriptRuntimeException.class)
    @ResponseBody
    public JSONObject exc(ScriptRuntimeException e)
    {
        Log.error(e.toStackString());

        JSONObject res = new JSONObject();
        res.put("result", "fail");
        res.put("reason", e.getMessage());

        return res;
    }

    protected Object call(String host, String uri, HttpSession session, JSONObject param)
    {
        Log.debug(host + "/" + uri + " <== " + param.toString());

        Gateway gateway = gatewaySrv.getGateway(uri);

        if (gateway == null)
            throw new RuntimeException(uri + " not found");

        // TODO: 此处需要对open开头的uri进行签名验证
        if(uri.startsWith("open/")) {
            String key = (String) param.get("key");
            String code = (String) param.get("code");

            if(key == null || code == null || gateway.getEnv() == null || gateway.getEnv().getStack() == null || gateway.getEnv().getStack().get("SIGN_CODE") == null)
                throw new RuntimeException(uri + " unauthorized access");

            JSONObject signCode = JSONObject.parseObject(JSONObject.toJSONString(gateway.getEnv().getStack().get("SIGN_CODE")));
            if(signCode.getJSONObject(key) == null || !code.equals(signCode.getJSONObject(key).getString(uri)))
                throw new RuntimeException(uri + " unauthorized access");

            /*JSONObject requestBody = param.getJSONObject("requestBody");
            param.remove("requestBody");
            if(requestBody != null)
                param.putAll(requestBody);*/
        }

        Stack root = gateway.getEnv().getStack();

        if (gateway.isLogin())
        {
//            System.out.println(session.getId());
//            Enumeration<String> str = session.getAttributeNames();
//            while (str.hasMoreElements())
//            {
//                String s = str.nextElement();
//                System.out.println(s + " = " + session.getAttribute(s));
//            }

            Long userId = (Long)session.getAttribute("userId");
            if (userId == null)
                throw new RuntimeException("not login - " + uri);
        }

        //With --> owner=memberId,platformId=PLATFORM_ID,将接口交易需要附加参数从session中取出，并传递至交易中
        if (gateway.getWith() != null)
        {
            for (Map.Entry<String, String> w : ((Map<String, String>)gateway.getWith()).entrySet())
            {
                String var = w.getValue();
                if (var.startsWith("#"))
                {
                    param.put(w.getKey(), var.substring(1));
                }
                else
                {
                    Object val = root == null ? null : root.get(var);
                    if (val == null)
                        val = session.getAttribute(var);
                    param.put(w.getKey(), val);
                }
            }
        }

        Object val = null;

        //Forward --> 0无 1微服务 2本地重定向 3远程链接 4?
        //请求服务在前，脚本为整理结果
        if (gateway.getForward() == Gateway.REQUEST_MICRO_SERVICE)
        {
            String forwardTo = gateway.getForwardTo(uri);

            int p2 = forwardTo.indexOf("/");
            //调用forwardTo服务接口
            JSONObject json = sv.req(forwardTo.substring(0, p2), forwardTo.substring(p2 + 1), param);

            if (!"success".equals(json.getString("result")))
                throw new RuntimeException(json.getString("reason"));

            val = JSON.toJSON(json.get("content"));
        }

        Script script = gateway.getScript();
        if (script != null)
        {
            Stack stack = new Stack(root);
            stack.set("self", val == null ? param : val);
            stack.set("SESSION", new SessionAdapter(session));
            
            try
            {
                val = script.run(stack);//script 执行中将可以获取使用到self(当前交易请求参数),session参数
            }
            catch (ScriptRuntimeException e1)
            {
                if (gateway.isMonitor())
                    gatewaySrv.onError(e1.getFactors(), e1.toStackString(), uri, e1);

                throw e1;
            }
            catch (Exception e)
            {
                if (gateway.isMonitor())
                    gatewaySrv.onError(null, e.getMessage(), uri, e);

                throw e;
            }
        }

        //Forward --> 0无 1微服务 2本地重定向 3远程链接 4?
        //请求服务在后，脚本为预处理参数
        if (gateway.getForward() == Gateway.FORWARD_MICRO_SERVICE)
        {
            if (val instanceof Map)
                param.putAll((Map)val);

            String forwardTo = gateway.getForwardTo(uri);

            int p2 = forwardTo.indexOf("/");
          //调用forwardTo服务接口
            JSONObject json = sv.req(forwardTo.substring(0, p2), forwardTo.substring(p2 + 1), param);

            if (!"success".equals(json.getString("result")))
                throw new RuntimeException(json.getString("reason"));

            val = json.get("content");
        }

        String str = null;
        if (val != null)
        {
            str = val.toString();
            if (str.length() > 1024)
                str = str.substring(0, 1024) + " ......";
        }

        Log.debug(host + "/" + uri + " ==> " + str);

        return val;
    }

    protected JSONObject getParam(HttpServletRequest req)
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

    @RequestMapping("/${gatedir}**/*.json")
    @ResponseBody
    @CrossOrigin
    public JSONObject callJson(HttpServletRequest req)
    {
        String uri = req.getRequestURI();
        uri = uri.substring(1 + gateDir.length());

//        long t1 = System.currentTimeMillis();
//        Log.debug(uri + " => " + t1);

        JSONObject param = getParam(req);
        param.put("URI", uri);

        HttpSession session = req.getSession();

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", call(req.getServerName() + ":" + req.getServerPort(), uri, session, param));

//        Log.debug(uri + " <= " + t1);

        return res;
    }

    @RequestMapping("/${gatedir}**/*.html")
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
                e1.printStackTrace();
            }
        }
    }

    @RequestMapping("/${gatedir}**/*.do")
    @CrossOrigin
    public String callAction(HttpServletRequest req)
    {
        String uri = req.getRequestURI();
        uri = uri.substring(1 + gateDir.length());

        JSONObject param = getParam(req);
        param.put("URI", uri);

        HttpSession session = req.getSession();
        return call(req.getServerName() + ":" + req.getServerPort(), uri, session, param).toString();
    }

    @RequestMapping("/${gatedir}**/*.file")
    @ResponseBody
    @CrossOrigin
    public JSONObject file(HttpServletRequest req, @RequestParam("file") List<MultipartFile> files)
    {
        new File(tempDir).mkdirs();

        Map map = new HashMap();

        for (MultipartFile file : files)
        {
            File ff = new File(tempDir + file.getOriginalFilename() + "." + System.currentTimeMillis());

            try (InputStream is = file.getInputStream())
            {
                Disk.saveToDisk(is, ff);
                map.put(ff.getAbsolutePath(), file.getOriginalFilename());

                Log.info("upload => " + ff);
            }
            catch (Exception e)
            {
                Log.error(e);
            }
        }

        String uri = req.getRequestURI();
        uri = uri.substring(1 + gateDir.length());

        JSONObject param = new JSONObject();
        param.put("URI", uri);
        param.put("files", map);

        HttpSession session = req.getSession();

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", call(req.getServerName() + ":" + req.getServerPort(), uri, session, param));

        return res;
    }

    /**
     * 短连接跳转
     * @param req
     * @param linkId
     * @return
     */
    @RequestMapping("/${gatedir}t/{linkId}")
    @CrossOrigin
    public String callLink(HttpServletRequest req, @PathVariable String linkId)
    {
        Gateway gateway = gatewaySrv.getGateway("util/t.do");
        Stack root = gateway.getEnv().getStack();

        Script script = gateway.getScript();
        Stack stack = new Stack(root);
        stack.set("linkId", linkId);
        stack.set("append", req.getQueryString());

        try
        {
            return lerrain.tool.formula.Value.stringOf(script, stack);
        }
        catch (ScriptRuntimeException e1)
        {
            if (gateway.isMonitor())
                gatewaySrv.onError(e1.getFactors(), e1.toStackString(), req.getRequestURL().toString(), e1);

            throw e1;
        }
        catch (Exception e)
        {
            if (gateway.isMonitor())
                gatewaySrv.onError(null, e.getMessage(), req.getRequestURL().toString(), e);

            throw e;
        }
    }
}

