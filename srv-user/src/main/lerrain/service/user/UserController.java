package lerrain.service.user;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lerrain.service.user.service.ModuleService;
import lerrain.service.user.service.RoleService;
import lerrain.service.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

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
}
