package lerrain.service.user;

import java.util.ArrayList;
import java.util.List;

import lerrain.service.common.Log;
import lerrain.service.user.enm.RoleTypeEnum;
import lerrain.service.user.service.ModuleService;
import lerrain.service.user.service.RoleService;
import lerrain.service.user.service.UserService;
import lerrain.service.user.util.PasswordUtil;
import lerrain.tool.Common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

@Controller
public class UserController
{
    @Autowired
    UserService userSrv;

    @Autowired
    RoleService roleSrv;

    @Autowired
    ModuleService moduleSrv;

    @RequestMapping({ "/login.json" })
    @ResponseBody
    public JSONObject login(@RequestBody JSONObject json)
    {
        String loginName = json.getString("loginName");
        String password = json.getString("password");

        User user = userSrv.login(loginName, password);

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", user);

        return res;
    }

    @RequestMapping({ "/find_modules.json" })
    @ResponseBody
    public JSONObject findModules(@RequestBody JSONObject json)
    {
        List<Role> roles = new ArrayList<>();

        JSONArray list = json.getJSONArray("role");
        if (list != null) for (Object obj : list)
        {
            JSONObject roleMap = (JSONObject)obj;

            Long roleId = roleMap.getLong("id");
            if (roleId != null)
                roles.add(roleSrv.getRole(roleId));
            else if (roleMap.containsKey("code"))
                roles.add(roleSrv.getRole(roleMap.getString("code")));
        }

        JSONArray menu = new JSONArray();

        long lastParent = -1L;
        JSONArray pack = null;

        for (Module m : moduleSrv.findModules(roles))
        {
            if (m.getParentId() != lastParent)
            {
                Module p = moduleSrv.getModule(m.getParentId());

                JSONObject module = new JSONObject();
                module.put("id", p.getId());
                module.put("name", p.getName());
                module.put("code", p.getCode());
                module.put("link", p.getLink());
                menu.add(module);

                pack = new JSONArray();
                module.put("item", pack);

                lastParent = m.getParentId();
            }

            JSONObject module = new JSONObject();
            module.put("id", m.getId());
            module.put("name", m.getName());
            module.put("code", m.getCode());
            module.put("link", m.getLink());
            pack.add(module);
        }

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", menu);

        return res;
    }

    @RequestMapping("/list.json")
    @ResponseBody
    public JSONObject list(@RequestBody JSONObject p)
    {
        int from = Common.intOf(p.get("from"), 0);
        int num = Common.intOf(p.get("num"), 20);

        JSONArray list = new JSONArray();
        for (User user : userSrv.list(null, from, num))
        {
            JSONObject obj = new JSONObject();
            obj.put("id", user.getId());
            obj.put("name", user.getName());
            obj.put("status", user.getStatus());
            obj.put("extra", user.getExtra());

            list.add(obj);
        }

        JSONObject r = new JSONObject();
        r.put("list", list);
        r.put("total", userSrv.count(null));

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", r);

        return res;
    }

    @RequestMapping({ "/role.json" })
    @ResponseBody
    public JSONObject role(@RequestBody JSONObject json)
    {
        Long userId = json.getLong("userId");
        User user = userSrv.getUser(userId);

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", user.getRole());

        return res;
    }

    @RequestMapping({ "/role_all.json" })
    @ResponseBody
    public JSONObject roleAll()
    {
        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", roleSrv.getRoleList());

        return res;
    }

    @RequestMapping({ "/verify_password.json" })
    @ResponseBody
    public JSONObject verifyPassword(@RequestBody JSONObject json)
    {
        Long userId = json.getLong("userId");
        String pwd = json.getString("password");

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", userSrv.verifyPassword(userId, pwd));

        return res;
    }

    @RequestMapping({ "/set_password.json" })
    @ResponseBody
    public JSONObject setPassword(@RequestBody JSONObject json)
    {
        Long userId = json.getLong("userId");
        String pwd = json.getString("password");

        userSrv.updatePassword(userId, pwd);

        JSONObject res = new JSONObject();
        res.put("result", "success");

        return res;
    }

    // 开通云服账号
    @RequestMapping({ "/openAccount.json" })
    @ResponseBody
    public JSONObject openAccount(@RequestBody JSONObject json) {
        // 开通类型 1人员 2组织 登录名 密码 类型 手机号
        Long userId = json.getLong("userId");
        int type = json.getIntValue("type");
        List<Long> roleList = new ArrayList<Long>();
        if (1 == type) {
            roleList.add(RoleTypeEnum.YUNFUMEMBER.getId());
        } else if (2 == type) {
            roleList.add(RoleTypeEnum.YUNFUORG.getId());
        } else if (3 == type) {
            roleList.add(RoleTypeEnum.YUNFUORG.getId());
        }
        String loginName = json.getString("loginName");
        String password = json.getString("password");
        if (password == null) {
            password = PasswordUtil.createPassword();
        }
        password = Common.md5Of(password);
        boolean result = userSrv.openAccount(userId, type, loginName, password, roleList);
        Log.info("开通账号结果:" + result);
        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", result);

        return res;
    }

    @RequestMapping({ "/removeLogin.json" })
    @ResponseBody
    public JSONObject removeLogin(@RequestBody JSONObject json) {
        String loginName = json.getString("loginName");
        boolean flag = userSrv.removeLogin(loginName);
        JSONObject result = new JSONObject();
        result.put("result", "success");
        result.put("content", flag);
        return result;
    }

    @RequestMapping({ "/freezeUser.json" })
    @ResponseBody
    public JSONObject freezeUser(@RequestBody JSONObject json) {
        long userId = json.getLong("userId");
        int status = json.getIntValue("status");
        userSrv.updateStatus(new Long[] { userId }, status);
        JSONObject result = new JSONObject();
        result.put("result", "success");
        result.put("content", true);
        return result;
    }
    
    @RequestMapping({ "/currentUser.json" })
    @ResponseBody
    public JSONObject currentUser(@RequestBody JSONObject json) {
        String loginName = json.getString("loginName");
        Log.info("loginName:" + loginName);
        String userId = userSrv.getUserId(loginName);
        JSONObject result = new JSONObject();
        result.put("result", "success");
        result.put("content", userId);
        return result;
    }

    @RequestMapping({ "/cs/login.json" })
    @ResponseBody
    public JSONObject csLogin(@RequestBody JSONObject json) {
        JSONObject res = new JSONObject();
        JSONObject result = new JSONObject();
        result.put("type", "account");
        result.put("status", "notokey");
        result.put("currentAuthority", "admin");
        res.put("result", "success");
        try {
            String loginName = json.getString("userName");
            String password = json.getString("password");

            User user = userSrv.csLogin(loginName, password);
            if (user != null) {
                result.put("status", "ok");
            }
        } catch (Exception e) {
            result.put("result", "fail");
            Log.error(e);
        }
        res.put("content", result);
        return res;
    }
}
