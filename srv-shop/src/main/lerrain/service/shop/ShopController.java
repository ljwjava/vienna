package lerrain.service.shop;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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

    @RequestMapping("/shop/products.json")
    @ResponseBody
    public JSONObject list(JSONObject p)
    {
        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", null);

        return res;
    }
}
