package lerrain.service.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lerrain.service.common.Log;
import lerrain.service.user.enm.RoleTypeEnum;
import lerrain.service.user.service.ModuleService;
import lerrain.service.user.service.RoleService;
import lerrain.service.user.service.UserService;
import lerrain.service.user.service.WxUserService;
import lerrain.service.user.util.PasswordUtil;
import lerrain.tool.Common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

@Controller
public class UserController {
    @Autowired
    UserService   userSrv;

    @Autowired
    RoleService   roleSrv;

    @Autowired
    ModuleService moduleSrv;

    @Autowired
    WxUserService wxUserService;

    @RequestMapping({ "/login.json" })
    @ResponseBody
    public JSONObject login(@RequestBody JSONObject json) {
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
    public JSONObject findModules(@RequestBody JSONObject json) {
        List<Role> roles = new ArrayList<>();

        JSONArray list = json.getJSONArray("role");
        if (list != null)
            for (Object obj : list) {
                JSONObject roleMap = (JSONObject) obj;

                Long roleId = roleMap.getLong("id");
                if (roleId != null)
                    roles.add(roleSrv.getRole(roleId));
                else if (roleMap.containsKey("code"))
                    roles.add(roleSrv.getRole(roleMap.getString("code")));
            }

        JSONArray menu = new JSONArray();

        long lastParent = -1L;
        JSONArray pack = null;

        for (Module m : moduleSrv.findModules(roles)) {
            if (m.getParentId() != lastParent) {
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
    public JSONObject list(@RequestBody JSONObject p) {
        int from = Common.intOf(p.get("from"), 0);
        int num = Common.intOf(p.get("num"), 20);

        JSONArray list = new JSONArray();
        for (User user : userSrv.list(null, from, num)) {
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
    public JSONObject role(@RequestBody JSONObject json) {
        Long userId = json.getLong("userId");
        User user = userSrv.getUser(userId);

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", user.getRole());

        return res;
    }

    @RequestMapping({ "/role_all.json" })
    @ResponseBody
    public JSONObject roleAll() {
        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", roleSrv.getRoleList());

        return res;
    }

    @RequestMapping({ "/verify_password.json" })
    @ResponseBody
    public JSONObject verifyPassword(@RequestBody JSONObject json) {
        Long userId = json.getLong("userId");
        String pwd = json.getString("password");

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", userSrv.verifyPassword(userId, pwd));

        return res;
    }

    @RequestMapping({ "/set_password.json" })
    @ResponseBody
    public JSONObject setPassword(@RequestBody JSONObject json) {
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
            roleList.add(RoleTypeEnum.YUNFUENTERPRISE.getId());
        } else if (3 == type) {
            roleList.add(RoleTypeEnum.YUNFUORG.getId());
        } else if (4 == type) {
            roleList.add(RoleTypeEnum.YUNFUBD.getId());
        }
        String loginName = json.getString("loginName");
        String password = null;
        String md5Password = json.getString("md5Password");
        if (md5Password != null) {
            password = md5Password;
        } else {
            password = json.getString("password");
            if (password == null) {
                password = PasswordUtil.createPassword();
            }
            password = Common.md5Of(password);
        }
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
        JSONArray userId = json.getJSONArray("userId");
        Long[] userIds = new Long[userId.size()];
        List<Long> userList = JSONObject.parseArray(userId.toJSONString(), Long.class);
        for (int i = 0; i < userList.size(); i++) {
            userIds[i] = userList.get(i);
        }
        int status = json.getIntValue("status");
        userSrv.updateStatus(userIds, status);
        JSONObject result = new JSONObject();
        result.put("result", "success");
        result.put("content", true);
        return result;
    }

    @RequestMapping({ "/currentUser.json" })
    @ResponseBody
    public JSONObject currentUser(@RequestBody JSONObject json) {
        String loginName = json.getString("loginName");
        if(StringUtils.isEmpty(loginName)) {
        	loginName = json.getString("user/loginName");
        }
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
	            if(user.getStatus() == 9) {
	                result.put("type", "freezeAccount");
	            } else {
	                result.put("status", "ok");
	                result.put("userId", user.getId());
	                result.put("userType", user.getType());
	            }
            }
        } catch (Exception e) {
            result.put("result", "fail");
            Log.error(e);
        }
        res.put("content", result);
        return res;
    }

    // 开通云服账号
    @RequestMapping({ "/batchOpenAccount.json" })
    @ResponseBody
    public JSONObject batchOpenAccount(@RequestBody JSONObject json) {
        // 开通类型 1人员 2组织 登录名 密码 类型 手机号
        JSONObject res = new JSONObject();
        JSONArray array = json.getJSONArray("memberInfo");
        try {
            for (int i = 0; i < array.size(); i++) {
                try {
                    JSONObject j = array.getJSONObject(i);
                    Long userId = j.getLong("id");
                    int type = json.getIntValue("type");
                    List<Long> roleList = new ArrayList<Long>();
                    if (1 == type || 0 == type) {
                        roleList.add(RoleTypeEnum.YUNFUMEMBER.getId());
                    } else if (2 == type) {
                        roleList.add(RoleTypeEnum.YUNFUORG.getId());
                    } else if (3 == type) {
                        roleList.add(RoleTypeEnum.YUNFUORG.getId());
                    } else if (4 == type) {
                        roleList.add(RoleTypeEnum.YUNFUBD.getId());
                    }
                    String loginName = j.getString("mobile");
                    String password = j.getString("password");
                    String orgPassword = null;
                    if (password == null) {
                        password = PasswordUtil.createPassword();
                        orgPassword = password;
                    }
                    password = Common.md5Of(password);
                    boolean result = userSrv.openAccount(userId, type, loginName, password, roleList);
                    Log.info("开通账号结果:" + result);
                    if(result) {
                    	j.put("password", orgPassword);
                    }
                } catch (Exception e1) {
                    Log.error(e1);
                }
            }
        } catch (Exception e) {
            Log.error(e);
        }
        res.put("result", "success");
        res.put("content", array);
        Log.info("批量开通账返回："+JSON.toJSONString(res));
        return res;
    }

    @RequestMapping({ "/wxuser.json" })
    @ResponseBody
    public JSONObject queryWxUser(@RequestBody JSONObject params) {
        JSONObject result = new JSONObject();
        Log.info("params=====" + JSON.toJSONString(params));
        JSONObject res = new JSONObject();
        WxUser user = new WxUser();
        user.setMobile(params.getString("mobile"));
        user.setName(params.getString("name"));
        Integer total = wxUserService.countWxUser(user);
        if (total == 0) {
            result.put("result", "success");
            result.put("content", res);
            return result;
        }
        Integer start = 0;
        Integer limit = params.getInteger("pageSize") == null ? 10 : params.getInteger("pageSize");
        if (params.getInteger("currentPage") != null) {
            start = (params.getInteger("currentPage") - 1) * limit;
        }
        user.setStart(start);
        user.setLimit(limit);
        List<WxUser> listWxUser = wxUserService.listWxUser(user);
        if (listWxUser != null && !listWxUser.isEmpty()) {
            for (int i = 0; i < listWxUser.size(); i++) {
                listWxUser.get(i).setIndex(i);
            }
        }
        res.put("list", listWxUser);
        Map<String, Object> pagination = new HashMap<>();
        pagination.put("total", total);
        pagination.put("current", params.getInteger("currentPage") == null ? 1 : params.getInteger("currentPage"));
        res.put("pagination", pagination);
        result.put("result", "success");
        result.put("content", res);
        return result;

    }
    
    @RequestMapping({ "/checkLoginName.json" })
    @ResponseBody
    public JSONObject checkLoginName(@RequestBody JSONObject json) {
        String loginName = json.getString("loginName");
        boolean flag = userSrv.isExisted(loginName);
        JSONObject result = new JSONObject();
        result.put("result", "success");
        result.put("content", flag);
        return result;
    }
    

    @RequestMapping({ "/findWxuserByOpendId.json" })
    @ResponseBody
    public JSONObject queryWxUserByOpenid(@RequestBody JSONObject params) {
        Log.info("params=====" + JSON.toJSONString(params));
        JSONObject result = new JSONObject();
        if (params.getString("openId") == null) {
            result.put("result", "false");
            return result;
        }
        WxUser user = new WxUser();
        user.setOpenId(params.getString("openId"));
        List<WxUser> listWxUser = wxUserService.listWxUser(user);
        if (listWxUser != null && !listWxUser.isEmpty()) {
            result.put("content", listWxUser.get(0));
        } else {
            user.setName(params.getString("name"));
            user.setMobile(params.getString("mobile"));
            user.setUserId(params.getLong("userId"));
            user.setAuthStatus("1");
            wxUserService.insert(user);
        }
        result.put("result", "success");
        return result;
    }

    @RequestMapping({ "/update_wxuser.json" })
    @ResponseBody
    public JSONObject updateWxUserByOpenid(@RequestBody WxUser user) {
        Log.info("params=====" + JSON.toJSONString(user));
        JSONObject result = new JSONObject();
        if (user.getOpenId() == null) {
            result.put("result", "false");
            return result;
        }
        wxUserService.updateByOpenId(user);
        result.put("result", "success");
        return result;
    }
}
