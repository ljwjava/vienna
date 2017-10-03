package lerrain.service.platform;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lerrain.tool.Common;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class PlatformController
{
    @Autowired PlatformService platformSrv;

    @RequestMapping("/health")
    @ResponseBody
    @CrossOrigin
    public String health()
    {
        return "success";
    }

    @RequestMapping("/reset")
    @ResponseBody
    public JSONObject reset()
    {
        platformSrv.initiate();

        JSONObject res = new JSONObject();
        res.put("result", "success");

        return res;
    }

    @RequestMapping({ "/do/{operate}.json" })
    @ResponseBody
    public JSONObject perform(@PathVariable String operate, @RequestBody JSONObject json)
    {
        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", platformSrv.perform(getPlatform(json), operate, json));

        return res;
    }

    @RequestMapping({ "/callback/{key}.json" })
    @ResponseBody
    public JSONObject callback(@PathVariable String key, @RequestBody JSONObject json)
    {
        JSONObject res = new JSONObject();

        try
        {
            res.put("result", "success");
            res.put("content", platformSrv.callback(key, json));
        }
        catch (Exception e)
        {
            e.printStackTrace();

            res.put("result", "fail");
            res.put("reason", e.getMessage());
        }

        return res;
    }

    private Platform getPlatform(JSONObject json)
    {
        Long platformId = json.getLong("platformId");
        if (platformId != null)
            return platformSrv.getPlatform(platformId);
        else
            return platformSrv.getPlatform(json.getString("channel"));
    }

    @RequestMapping("/list.json")
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
}
