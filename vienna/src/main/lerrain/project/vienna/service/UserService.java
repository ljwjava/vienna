package lerrain.project.vienna.service;

import com.alibaba.fastjson.JSONObject;
import lerrain.service.common.ServiceMgr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserService
{
    @Autowired
    ServiceMgr sv;

    public String getUserId(HttpSession session)
    {
        return (String)session.getAttribute("userId");
    }

    public boolean login(HttpSession session, String loginName, String pwd)
    {
        JSONObject req = new JSONObject();
        req.put("loginName", loginName);
        req.put("password", pwd);

        JSONObject res = sv.req("user", "login.json", req);
//        JSONObject res = userClient.redirect("login", req);
        if ("success".equals(res.get("result")))
        {
            JSONObject user = res.getJSONObject("content");
            session.setAttribute("userId", user.get("id"));
            session.setAttribute("userRole", user.get("role"));
            session.setAttribute("platformId", user.get("platformId"));

            return true;
        }

        return false;
    }

    public boolean isLogin(HttpSession session)
    {
        return session.getAttribute("userId") != null;
    }

    public void logout(HttpSession session)
    {
        session.invalidate();
    }

    public Map getUserInfo(HttpSession session)
    {
        Map map = new HashMap();
        map.put("menu", getModules(session));

        return map;
    }

    public List getModules(HttpSession session)
    {
        JSONObject req = new JSONObject();
        req.put("role", session.getAttribute("userRole"));

        JSONObject res = sv.req("user", "find_modules.json", req);
//        JSONObject res = userClient.redirect("find_modules", req);
        return res.getJSONArray("content");
    }
}
