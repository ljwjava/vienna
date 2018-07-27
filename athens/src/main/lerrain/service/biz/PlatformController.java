package lerrain.service.biz;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import lerrain.service.common.Log;

import org.apache.commons.lang.StringUtils;
import lerrain.service.common.ServiceMgr;
import lerrain.tool.Network;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

@Controller
public class PlatformController
{
    @Autowired
    GatewayController gc;

    @Autowired
    ServiceMgr serviceMgr;

    Map<String, Long> kv = new HashMap<>();

    @RequestMapping("/wx/**/*.json")
    @ResponseBody
    @CrossOrigin
    public JSONObject wx(HttpServletRequest req)
    {
        String uri = req.getRequestURI();
        uri = uri.substring(uri.indexOf("/", 1) + 1);

        JSONObject param = gc.getParam(req);
        HttpSession session = req.getSession();

        Long userId = (Long)session.getAttribute("userId");
        if (userId == null)
        {
            String userKey = param.getString("userKey");
            if (userKey != null)
            {
                synchronized (kv)
                {
                    if (kv.containsKey(userKey))
                        userId = kv.get(userKey);
                }

                if (userId == null)
                {
                    JSONObject req2 = new JSONObject();
                    req2.put("userKey", userKey);
                    req2.put("appCode", "proposal");

                    JSONObject res2 = serviceMgr.req("user", "app/user.json", req2);
                    if ("success".equals(res2.getString("result")))
                    {
                        res2 = res2.getJSONObject("content");
                        userId = res2.getLong("userId");
                        if (userId == null)
                            userId = res2.getLong("originalId");

                        synchronized (kv)
                        {
                            if (kv.size() > 100000)
                                kv.clear();

                            kv.put(userKey, userId);
                        }
                    }
                }
            }
            else if (uri.equals("login.json"))
            {
                String jsCode = param.getString("jsCode");
                JSONObject r2 = JSONObject.parseObject(Network.request("https://api.weixin.qq.com/sns/jscode2session?appid=wxc4140eb67743b792&secret=93f724e8540a1a0dec6c4413bd0688ca&js_code="+jsCode+"&grant_type=authorization_code"));

                JSONObject res = new JSONObject();
                res.put("result", "success");
                res.put("content", r2);
                return res;
            }

            if (userId == null)
                throw new RuntimeException("can't init user");

            session.setAttribute("userId", userId);
            session.setAttribute("memberId", userId);
            session.setAttribute("platformId", 9L);
        }

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", gc.call(req.getServerName() + ":" + req.getServerPort(), uri, session, param));

        return res;
    }

    @RequestMapping("/app/**/*.json")
    @ResponseBody
    @CrossOrigin
    public JSONObject app(HttpServletRequest req)
    {
        String uri = req.getRequestURI();
        uri = uri.substring(uri.indexOf("/", 1) + 1);
        if (uri.startsWith("/"))
            uri = uri.substring(1);

        JSONObject param = gc.getParam(req);
        HttpSession session = req.getSession();

        Long userId = (Long)session.getAttribute("userId");
        if (userId == null)
        {
            //userKey（用处类似sessionId）登录时生成，同时把其与userId的关联关系保存在内存里，userKey回传前端，前端保存在客户端内存，每次请求时带上
            String userKey = param.getString("userKey");
            if (userKey != null)
            {
                userId = 1L; //这里通过userKey获取userId

                session.setAttribute("userId", userId);
                session.setAttribute("memberId", userId);
                session.setAttribute("platformId", 9L);
            }
        }

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", gc.call(req.getServerName() + ":" + req.getServerPort(), uri, session, param));

        return res;
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
            JSONObject param = gc.getParam(req);
            HttpSession session = req.getSession();

            Long memberId = param.getLong("owner");
            if (memberId == null)
                memberId = param.getLong("accountId");
            if (memberId != null)
            {
                session.setAttribute("userId", memberId);
                session.setAttribute("memberId", memberId);
                session.setAttribute("platformId", 3L);
            }

            res.put("isSuccess", true);
            res.put("result", gc.call(req.getServerName() + ":" + req.getServerPort(), uri, session, param));
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

        JSONObject param = gc.getParam(req);
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
                val.put("result", gc.call(req.getServerName() + ":" + req.getServerPort(), uri, session, param));
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
    
    @RequestMapping(value={"/cs/**/*.json","/cs/*.json"})
    @ResponseBody
    @CrossOrigin
    public JSONObject cs(HttpServletRequest req,HttpServletResponse rsp)
    {
        String uri = req.getRequestURI();
        uri = uri.substring(1);

        JSONObject res = new JSONObject();
        try
        {
            String origin = req.getHeader("Origin");
            Log.info("origin:"+origin);
            if(origin == null) {
                origin = req.getHeader("Referer");
            }
            rsp.setHeader("Access-Control-Allow-Origin", origin);            // 允许指定域访问跨域资源
            rsp.setHeader("Access-Control-Allow-Credentials", "true");       // 允许客户端携带跨域cookie，此时origin值不能为“*”，只能为指定单一域名
            
            if(RequestMethod.OPTIONS.toString().equals(req.getMethod())) {
                String allowMethod = req.getHeader("Access-Control-Request-Method");
                String allowHeaders = req.getHeader("Access-Control-Request-Headers");
                rsp.setHeader("Access-Control-Max-Age", "86400");            // 浏览器缓存预检请求结果时间,单位:秒
                rsp.setHeader("Access-Control-Allow-Methods", allowMethod);  // 允许浏览器在预检请求成功之后发送的实际请求方法名
                rsp.setHeader("Access-Control-Allow-Headers", allowHeaders); // 允许浏览器发送的请求消息头
                return null;
            }
            Cookie[] cookies = req.getCookies();
            JSONObject param = gc.getParam(req);
            HttpSession session = req.getSession();
            JSONObject cookieJson = new JSONObject();
            if(cookies != null && cookies.length > 0) {
                for(int i=0;i<cookies.length;i++){
                	String name = cookies[i].getName();
                	String value = cookies[i].getValue();
                	cookieJson.put(name, value);
                }
            }
//            Log.info("cookieJson:"+JSON.toJSONString(cookieJson));
            if(req.getRequestURI().startsWith("/cs/") && !req.getRequestURI().startsWith("/cs/open") && !req.getRequestURI().startsWith("/cs/getMenu.json") && !req.getRequestURI().startsWith("/cs/login.json")) {
            	try {
            		Log.info("url:"+req.getRequestURI());
                    Object csUserId = session.getAttribute("csUserId");
                    if(csUserId == null) {
                		Log.info("session失效，跳转到登录页");
                		rsp.setStatus(299);
                		return null;
                    }
            	} catch(Exception e) {
            		Log.error(e);
            	}
            }
            res.put("result", "success");
            res.put("content", gc.call(req.getServerName() + ":" + req.getServerPort(), uri, session, param));
        }
        catch (Exception e)
        {
        	res.put("result", "false");
            res.put("errorCode", 101);
            
            res.put("errorMsg", e.getMessage());
        }

        return res;
    }    
}

