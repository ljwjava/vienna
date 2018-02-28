package lerrain.service.product;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.Map;

@Controller
public class ProductController
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
