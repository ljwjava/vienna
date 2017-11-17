package lerrain.service.sale;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lerrain.service.common.ServiceMgr;
import lerrain.tool.Common;
import lerrain.tool.script.Script;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;

@Controller
public class SaleController
{
    @Autowired WareService wareSrv;
    @Autowired PlatformService platformSrv;
    @Autowired VendorService vendorSrv;
    @Autowired PlatformBizService platformBizSrv;
    @Autowired ServiceMgr serviceMgr;
    @Autowired CmsService cmsSrv;

    /*@RequestMapping("/health")
    @ResponseBody
    @CrossOrigin
    public String health()
    {
        return "success";
    }*/

    @PostConstruct
    @RequestMapping("/reset")
    @ResponseBody
    public String reset()
    {
        wareSrv.reset();
        platformSrv.reset();
        cmsSrv.reset();

        return "success";
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
    public JSONObject callbackJson(@PathVariable String key, @RequestBody(required=false) JSONObject json)
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

    @RequestMapping({ "/callback/{key}.html", "/callback/{key}.do" })
    @ResponseBody
    public String callbackHtml(@PathVariable String key, @RequestBody(required=false) JSONObject json)
    {
        try
        {
            Object str = platformBizSrv.callback(key, json);
            return str == null ? "" : str.toString();
        }
        catch (Exception e)
        {
            throw e;
        }
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

    @RequestMapping("/replace.json")
    @ResponseBody
    public JSONObject replace(@RequestBody JSONObject req)
    {
        String scriptStr = req.getString("script");
        String funcName = req.getString("name");

        Platform p = getPlatform(req);
        p.putVar(funcName, Script.scriptOf(scriptStr));

        JSONObject res = new JSONObject();
        res.put("result", "success");

        return res;
    }
}
