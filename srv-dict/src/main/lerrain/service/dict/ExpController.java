package lerrain.service.dict;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lerrain.service.dict.ip.DipSeeker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class ExpController
{
    @Autowired
    DipSeeker dipSeeker;

    @RequestMapping("/id/range.do")
    @ResponseBody
    @CrossOrigin
    public JSONObject range(@RequestParam("code") String code)
    {
        JSONObject res = new JSONObject();
        res.put("result", "success");
//        res.put("content", dipSeeker.find(ip));

        return res;
    }

    @RequestMapping("/ip/find.do")
    @ResponseBody
    @CrossOrigin
    public JSONObject ip(@RequestParam("ip") String ip)
    {
        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", dipSeeker.find(ip));

        return res;
    }

    @RequestMapping("/ip/find.json")
    @ResponseBody
    @CrossOrigin
    public JSONObject ipJson(@RequestBody JSONObject ip)
    {
        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", dipSeeker.find(ip.getString("ip")));

        return res;
    }

    @RequestMapping("/ip/query.json")
    @ResponseBody
    @CrossOrigin
    public JSONObject batch(@RequestBody JSONObject ip)
    {
        JSONArray list = ip.getJSONArray("ip");
        JSONArray r = new JSONArray();
        for (int i=0;i<list.size();i++)
            r.add(dipSeeker.find(list.getString(i)));

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", r);

        return res;
    }
}
