package lerrain.service.sale;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lerrain.service.common.ServiceMgr;
import lerrain.service.sale.pack.Lifeins;
import lerrain.service.sale.pack.PackIns;
import lerrain.service.sale.pack.PackService;
import lerrain.service.sale.pack.PackUtil;
import lerrain.tool.Common;
import lerrain.tool.formula.Formula;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class SaleController
{
    @Autowired WareService wareSrv;
    @Autowired PackService packSrv;
    @Autowired CmsService cmsSrv;
    @Autowired VendorService vendorSrv;

    @Autowired ServiceMgr serviceMgr;

    @PostConstruct
    @RequestMapping("/reset")
    @ResponseBody
    public String reset()
    {
        wareSrv.reset();
        packSrv.reset();
        cmsSrv.reset();
        vendorSrv.reset();

        return "success";
    }

    @RequestMapping("/list.json")
    @ResponseBody
    public JSONObject premium(@RequestBody JSONObject json)
    {
        Long platformId = json.getLong("platformId");

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", wareSrv.find(platformId, json.getString("tag")));

        return res;
    }

    @RequestMapping("/view.json")
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
//        JSONObject json = serviceMgr.req("lifeins", "pack/view.json", req);
//        JSONObject content = json.getJSONObject("content");

        JSONObject content = PackUtil.toJson(getPack(req));

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


    @RequestMapping("/perform.json")
    @ResponseBody
    public JSONObject perform(@RequestBody JSONObject msg)
    {
        Object r = null;

        String opt = msg.getString("opt");
        JSONObject content = msg.getJSONObject("content");
        PackIns packIns = getPack(content);

        Formula script = packIns.getOpts() == null ? null : packIns.getOpts().get(opt);

        if (script != null)
        {
            r = packSrv.perform(script, packIns, content);
        }
        else if ("try".equals(opt) || "premium".equals(opt))
        {
            r = packSrv.getPrice(packIns, content);
        }
        else if ("plan".equals(opt))
        {
            JSONObject json = new JSONObject();
            json.put("planId", packIns.getReferKey());
            json.put("script", null);
            json.put("with", Lifeins.translate(packIns, content));

            return serviceMgr.req("lifeins", "plan/perform.json", json);
        }
        else if ("cms".equals(opt))
        {
            Long platformId = content.getLong("platformId");
            String group = content.getString("group");
            Long packId = content.getLong("packId");
            String payFreq = content.getString("payFreq");
            String payPeriod = content.getString("payPeriod");

            List<CmsDefine> list = cmsSrv.getCommissionRate(platformId, group, packId, payFreq, payPeriod);

            if (list != null)
            {
                r = new JSONArray();
                for (CmsDefine c : list)
                {
                    Map m = new HashMap();
                    m.put("self", c.getSelfRate());
                    m.put("parent", c.getParentRate());
                    m.put("unit", c.getUnit());
                    m.put("freeze", c.getFreeze());
                    m.put("memo", c.getMemo());

                    ((JSONArray)r).add(m);
                }
            }
        }

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", r);

        return res;
    }

    private PackIns getPack(Map<String, Object> vals)
    {
        PackIns pack = null;

        if (vals.containsKey("packId"))
            pack = packSrv.getPack(Common.toLong(vals.get("packId")));
        else if (vals.containsKey("productId"))
            pack = packSrv.getPack(Common.toLong(vals.get("productId")));
        else if (vals.containsKey("packCode"))
            pack = packSrv.getPack((String)vals.get("packCode"));

        return pack;
    }

    @RequestMapping({ "/pack/view.json" })
    @ResponseBody
    public JSONObject viewPack(@RequestBody JSONObject json)
    {
        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", PackUtil.toJson(getPack(json)));

        return res;
    }
}
