package lerrain.project.vienna;

import com.alibaba.fastjson.JSONObject;
import lerrain.project.vienna.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@Controller
public class UserController
{
    @Autowired
    UserService userSrv;

    @RequestMapping("/user/login.json")
    @ResponseBody
    @CrossOrigin
    public JSONObject login(HttpSession session, @RequestBody JSONObject req)
    {
        if (!userSrv.login(session, req.getString("loginName"), req.getString("password")))
            throw new RuntimeException("login fail");

        JSONObject res = new JSONObject();
        res.put("result", "success");

        return res;
    }

    @RequestMapping("/user/logout.do")
    @ResponseBody
    @CrossOrigin
    public JSONObject logout(HttpSession session)
    {
        userSrv.logout(session);

        JSONObject res = new JSONObject();
        res.put("result", "success");

        return res;
    }

    @RequestMapping("/user/info.do")
    @ResponseBody
    @CrossOrigin
    public JSONObject info(HttpSession session)
    {
        if (!userSrv.isLogin(session))
            throw new RuntimeException("not login");

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", userSrv.getUserInfo(session));

        return res;
    }
}
