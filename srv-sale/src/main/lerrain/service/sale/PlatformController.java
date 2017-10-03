package lerrain.service.sale;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lerrain.tool.Common;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class PlatformController
{
    @Autowired
    PlatformService platformSrv;

    @RequestMapping("/platform/list.json")
    @ResponseBody
    public JSONObject list(@RequestBody JSONObject json)
    {
        String search = json.getString("search");
        int from = Common.intOf(json.get("from"), 0);
        int number = Common.intOf(json.get("number"), -1);

        JSONArray list = new JSONArray();
        for (Platform c : platformSrv.list(search, from, number))
        {
            JSONObject channel = new JSONObject();
            channel.put("id", c.getId());
            channel.put("code", c.getCode());
            list.add(channel);
        }

        JSONObject r = new JSONObject();
        r.put("total", platformSrv.count(search));
        r.put("list", list);

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", r);

        return res;
    }

    @RequestMapping("/platform/view_script.json")
    @ResponseBody
    public JSONObject viewScript(@RequestBody JSONObject json)
    {
        Long platformId = json.getLong("platformId");

        JSONObject r = new JSONObject();
        r.put("perform", platformSrv.getPerformScript(platformId));
        r.put("env", platformSrv.getEnvScript(platformId));

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", r);

        return res;
    }

    @RequestMapping("/platform/save_script.json")
    @ResponseBody
    public JSONObject saveScript(@RequestBody JSONObject json)
    {
        Long platformId = json.getLong("platformId");
        String env = json.getString("env");
        String perform = json.getString("perform");

        if (!Common.isEmpty(Common.trimStringOf(env)))
            platformSrv.saveEnvScript(platformId, env);
        if (!Common.isEmpty(Common.trimStringOf(perform)))
            platformSrv.savePerformScript(platformId, perform);

        JSONObject res = new JSONObject();
        res.put("result", "success");

        return res;
    }
}
