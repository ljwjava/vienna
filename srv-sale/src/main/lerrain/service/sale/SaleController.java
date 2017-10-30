package lerrain.service.sale;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lerrain.service.common.Log;
import lerrain.service.common.ServiceMgr;
import lerrain.tool.Common;
import lerrain.tool.script.Script;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class SaleController
{
    @Autowired WareService wareSrv;
    @Autowired PlatformService platformSrv;
    @Autowired VendorService vendorSrv;
    @Autowired PlatformBizService platformBizSrv;
    @Autowired ServiceMgr serviceMgr;

    @RequestMapping("/health")
    @ResponseBody
    @CrossOrigin
    public String health()
    {
        return "success";
    }

    @RequestMapping({ "/reset" })
    @ResponseBody
    public JSONObject reset()
    {
        wareSrv.initiate();
        platformSrv.initiate();

        JSONObject res = new JSONObject();
        res.put("result", "success");

        return res;
    }

    @RequestMapping({ "/perform.json" })
    @ResponseBody
    @Deprecated
    public JSONObject perform(@RequestBody JSONObject json)
    {
        JSONObject res = new JSONObject();

        try
        {
            res.put("result", "success");
            res.put("content", platformBizSrv.perform(getPlatform(json), json));
        }
        catch (Exception e)
        {
            e.printStackTrace();

            res.put("result", "fail");
            res.put("reason", e.getMessage()); //Common.getStackString(e));
        }

        return res;
    }

    @RequestMapping({ "/do/{operate}.json" })
    @ResponseBody
    public JSONObject perform(@PathVariable String operate, @RequestBody JSONObject json)
    {
        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", platformBizSrv.perform(getPlatform(json), operate, json));

        return res;
    }

//    @RequestMapping({ "/verify.json" })
//    @ResponseBody
//    public JSONObject verify(@RequestBody JSONObject json)
//    {
//        JSONObject res = new JSONObject();
//
//        try
//        {
//            res.put("result", "success");
//            res.put("content", platformBizSrv.verify(getPlatform(json), json));
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//
//            res.put("result", "fail");
//            res.put("reason", e.getMessage());
//        }
//
//        return res;
//    }
//
//    @RequestMapping({ "/apply.json" })
//    @ResponseBody
//    public JSONObject apply(@RequestBody JSONObject json)
//    {
//        JSONObject res = new JSONObject();
//        res.put("result", "success");
//        res.put("content", platformBizSrv.apply(getPlatform(json), json));
//
//        return res;
//    }
//
//    @RequestMapping({ "/status.json" })
//    @ResponseBody
//    public JSONObject status(@RequestBody JSONObject json)
//    {
//        return serviceMgr.req("order", "view.json", json);
//    }

    @RequestMapping({ "/callback/{key}.json" })
    @ResponseBody
    public JSONObject callback(@PathVariable String key, @RequestBody(required=false) JSONObject json)
    {
        JSONObject res = new JSONObject();

        try
        {
            res.put("result", "success");
            res.put("content", platformBizSrv.callback(key, json));
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

    @RequestMapping({ "/test.json" })
    @ResponseBody
    public JSONObject test(@RequestBody JSONObject json)
    {
        JSONObject res = new JSONObject();

        try
        {
            Script script = Script.scriptOf(json.getString("script"));

            res.put("result", "success");
            res.put("content", platformBizSrv.perform(getPlatform(json), script, json));
        }
        catch (Exception e)
        {
            res.put("result", "fail");
            res.put("reason", Common.getStackString(e));
        }

        return res;
    }

    @RequestMapping({ "/list.json" })
    @ResponseBody
    public JSONObject premium(@RequestBody JSONObject json)
    {
        Long platformId = json.getLong("platformId");

        if (platformId == null)
            platformId = getPlatform(json).getId();

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
        if (json.containsKey("wareId"))
        {
            Ware ware = wareSrv.getWare(json.getLong("wareId"));
            JSONObject wareJson = new JSONObject();
            wareJson.put("code", ware.getCode());
            json.put("ware", wareJson);
        }

        return json;
    }
}
