package lerrain.service.shop;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lerrain.tool.Common;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ShopController
{
    @Autowired
    ShopService productSrv;

    @RequestMapping("/reset")
    @ResponseBody
    public String reset()
    {
        return "success";
    }

    @RequestMapping("/commoditys.json")
    @ResponseBody
    public JSONObject list(@RequestBody JSONObject p)
    {
        int from = Common.intOf(p.get("from"), 0);
        int num = Common.intOf(p.get("num"), 10);

        JSONArray list = new JSONArray();
        for (Shop shop : productSrv.list(null, from, num))
        {
            JSONObject obj = (JSONObject)JSON.toJSON(shop);
            list.add(obj);
        }

        JSONObject r = new JSONObject();
        r.put("list", list);
        r.put("total", productSrv.count(null));

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", r);

        return res;
    }
}
