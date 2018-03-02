package lerrain.service.product.fee;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
public class FeeController
{
    @Autowired
    FeeService cs;

    @RequestMapping("/pay.json")
    @ResponseBody
    public JSONObject pay(@RequestBody JSONArray ids)
    {
        List<Long> idlist = new ArrayList<Long>();
        for (int i = 0; i < ids.size(); i++)
            idlist.add(ids.getLong(i));
        cs.pay(idlist);

        JSONObject res = new JSONObject();
        res.put("result", "success");

        return res;
    }

    @RequestMapping("/pay_all.json")
    @ResponseBody
    public JSONObject payAll(@RequestBody JSONObject c)
    {
        Long platformId = c.getLong("platformId");
        Long productId = c.getLong("productId");
        String bizNo = c.getString("bizNo");

        int fail = cs.payAll(platformId, productId, bizNo);

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", fail);

        return res;
    }


    @RequestMapping("/charge.json")
    @ResponseBody
    public JSONObject charge(@RequestBody JSONArray list)
    {
        cs.charge(list);

        JSONObject res = new JSONObject();
        res.put("result", "success");

        return res;
    }

    @RequestMapping("/list_fee.json")
    @ResponseBody
    public JSONObject listFeeDef(@RequestBody JSONObject c)
    {
        Long platformId = c.getLong("platformId");
        Long agencyId = c.getLong("agencyId");
        Long productId = c.getLong("productId");

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", cs.listFeeRate(platformId, agencyId, productId));

        return res;
    }

    @RequestMapping("/fee.json")
    @ResponseBody
    public JSONObject fee(@RequestBody JSONObject c)
    {
        Long platformId = c.getLong("platformId");
        Long productId = c.getLong("productId");
        Long agencyId = c.getLong("agencyId");

        String group = c.getString("group");
        Map factors = c.getJSONObject("factors");

        Date time = c.getDate("time");
        if (time == null)
            time = new Date();

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", cs.getFeeRate(platformId, agencyId, productId, group, factors, time));

        return res;
    }

}
