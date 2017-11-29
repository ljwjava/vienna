package lerrain.service.sale;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lerrain.service.common.ServiceMgr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PostConstruct;

@Controller
public class SaleController
{
    @Autowired WareService wareSrv;
    @Autowired VendorService vendorSrv;
    @Autowired ServiceMgr serviceMgr;

    @PostConstruct
    @RequestMapping("/reset")
    @ResponseBody
    public String reset()
    {
        wareSrv.reset();
        vendorSrv.reset();

        return "success";
    }

    @RequestMapping({ "/list.json" })
    @ResponseBody
    public JSONObject premium(@RequestBody JSONObject json)
    {
        Long platformId = json.getLong("platformId");

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", wareSrv.find(platformId, json.getString("tag")));

        return res;
    }

    @RequestMapping({ "/view.json" })
    @ResponseBody
    public JSONObject view(@RequestBody JSONObject json)
    {
        Ware c = json.containsKey("wareId") ? wareSrv.getWare(json.getLong("wareId")) : wareSrv.getWare(json.getString("wareCode"));

        JSONObject r = (JSONObject)JSON.toJSON(c);
        r.put("vendor", vendorSrv.getVendor(c.getVendorId()));

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", r);

        return res;
    }

    @RequestMapping("/detail.json")
    @ResponseBody
    public JSONObject detail(@RequestBody JSONObject req)
    {
        JSONObject json = serviceMgr.req("lifeins", "pack/view.json", req);
        JSONObject content = json.getJSONObject("content");

        if (req.containsKey("wareId"))
        {
            Ware ware = wareSrv.getWare(req.getLong("wareId"));
            JSONObject wareJson = new JSONObject();
            wareJson.put("code", ware.getCode());
            content.put("ware", wareJson);
        }

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", content);

        return res;
    }
}
