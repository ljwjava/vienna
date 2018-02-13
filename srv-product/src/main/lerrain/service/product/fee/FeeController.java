package lerrain.service.product.fee;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PostConstruct;
import java.util.*;

@Controller
public class FeeController
{
    @Autowired
    FeeService cs;

    @PostConstruct
    @RequestMapping("/reset")
    @ResponseBody
    public String reset()
    {
        cs.reset();
        return "success";
    }

    @RequestMapping("/payoff.json")
    @ResponseBody
    public JSONObject payoff(@RequestBody JSONArray list)
    {
        for (int i=0;i<list.size();i++)
        {
            Map map = list.getJSONObject(i);
            Fee commission = Fee.feeOf(map);

            if (commission.getAmount() > 0)
                cs.store(commission);
        }

        JSONObject res = new JSONObject();
        res.put("result", "success");

        return res;
    }

    @RequestMapping("/retry.json")
    @ResponseBody
    public JSONObject retry(@RequestBody JSONArray ids)
    {
        List<Long> idlist = new ArrayList<Long>();
        for (int i=0;i<ids.size();i++)
        {
            Long id = ids.getLong(i);
            idlist.add(id);
        }
        cs.retry(idlist);

        JSONObject res = new JSONObject();
        res.put("result", "success");

        return res;
    }

    @RequestMapping("/charge.json")
    @ResponseBody
    public JSONObject charge(@RequestBody JSONObject c)
    {
        Long platformId = c.getLong("platformId");
        Date time = c.getDate("updateTime");
        if (time == null)
            time = new Date();

        boolean result = cs.charge(platformId, time, c);

        JSONObject res = new JSONObject();
        res.put("result", result ? "success" : "fail");

        return res;
    }

    @RequestMapping("/calc.json")
    @ResponseBody
    public JSONObject calc(@RequestBody JSONObject c)
    {
        Long platformId = c.getLong("platformId");

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", cs.calc(platformId, new Date(), c));

        return res;
    }

    @RequestMapping("/list_feedef.json")
    @ResponseBody
    public JSONObject listFeeDef(@RequestBody JSONObject c)
    {
        Long platformId = c.getLong("platformId");
        Long productId = c.getLong("productId");

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", cs.loadFeeDefine(platformId, productId));

        return res;
    }

    @RequestMapping("/fee.json")
    @ResponseBody
    public JSONObject fee(@RequestBody JSONObject c)
    {
        Long platformId = c.getLong("platformId");
        Long productId = c.getLong("productId");
        String bizNo = c.getString("bizNo");

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", cs.getFee(platformId, productId, bizNo));

        return res;
    }
}
