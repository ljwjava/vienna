package lerrain.service.user;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lerrain.service.common.Log;
import lerrain.service.user.enm.RoleTypeEnum;
import lerrain.service.user.service.*;
import lerrain.service.user.util.PasswordUtil;
import lerrain.tool.Common;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class AppController
{
    @Autowired
    AppService appSrv;

    @RequestMapping("/app/user.json")
    @ResponseBody
    public JSONObject user(@RequestBody JSONObject json)
    {
        Log.info(json);

        String userKey = json.getString("userKey");
        String appCode = json.getString("appCode");

        AppUser user = appSrv.find(userKey, appCode);

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", user);

        return res;
    }
}
