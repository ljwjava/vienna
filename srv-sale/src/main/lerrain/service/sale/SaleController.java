package lerrain.service.sale;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lerrain.service.common.Log;
import lerrain.service.common.ServiceMgr;
import lerrain.tool.Common;
import lerrain.tool.formula.Formula;
import lerrain.tool.script.Script;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class SaleController
{
    @Autowired
    SaleService saleSrv;

    @Autowired
    ServiceMgr serviceMgr;

    @Value("${env}")
    String srvEnv;

    @RequestMapping("/admin/address")
    @ResponseBody
    public String service(@RequestBody JSONObject req)
    {
        serviceMgr.reset(req);
        return "success";
    }

    @PostConstruct
    @RequestMapping("/reset")
    @ResponseBody
    public String reset()
    {
        Script.STACK_MESSAGE = !("prd".equalsIgnoreCase(srvEnv) || "uat".equalsIgnoreCase(srvEnv));
        Log.info("ENV: " + srvEnv + ", log of formula stack: " + Script.STACK_MESSAGE);

        saleSrv.reset();

        return "success";
    }

//    @RequestMapping("/list.json")
//    @ResponseBody
//    public JSONObject premium(@RequestBody JSONObject json)
//    {
//        Long platformId = json.getLong("platformId");
//
//        JSONObject res = new JSONObject();
//        res.put("result", "success");
//        res.put("content", saleSrv.find(platformId, json.getString("tag")));
//
//        return res;
//    }

    @RequestMapping("/view.json")
    @ResponseBody
    public JSONObject view(@RequestBody JSONObject json)
    {
        List<Long> pis = null;
        if(json.containsKey("packIds") && !Common.isEmpty(json.getString("packIds"))) {
            pis = new ArrayList<Long>();
            String pisStr = json.getString("packIds");

            String[] pisArr = pisStr.split(",");
            for (String piId : pisArr) {
                if(!Common.isEmpty(piId))
                    pis.add(Long.parseLong(piId));
            }
        }

        Ware c = json.containsKey("wareId") ? saleSrv.getWare(json.getLong("wareId"), pis) : saleSrv.getWare(json.getString("wareCode"), pis);

        JSONObject r = new JSONObject();
        r.put("id", c.getId());
        r.put("code", c.getCode());
        r.put("name", c.getName());
        r.put("abbrName", c.getAbbrName());
        r.put("remark", c.getRemark());
        r.put("logo", c.getLogo());
        r.put("price", c.getPrice());
        r.put("banner", c.getBanner());

        r.put("vendor", saleSrv.getVendor(c.getVendorId()));

        JSONArray detail = new JSONArray();
        for (PackIns pack : c.getDetail())
        {
            JSONObject p = new JSONObject();
            p.put("target", pack.getId());
            p.put("name", pack.getName());
            p.put("summary", pack.getShow());
            detail.add(p);
        }
        r.put("detail", detail);

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", r);

        return res;
    }

    @RequestMapping("/detail.json")
    @ResponseBody
    public JSONObject detail(@RequestBody JSONObject req)
    {
        PackIns pack = getPack(req);
        JSONObject content = PackUtil.toJson(pack, null);

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
            r = saleSrv.perform(script, packIns, content);
        }
        else if ("try".equals(opt) || "premium".equals(opt))
        {
            Map<String, Object> r1 = saleSrv.getPrice(packIns, content);

            if (packIns.isDynamicForm())
                r1.put("form", PackUtil.toForm(packIns, content));

            r = r1;
        }
        else if ("detail".equals(opt) || "plan".equals(opt)) //plan以后由detail替代，可以查看其他类型产品的详情
        {
            if (packIns.getPriceType() == PackIns.PRICE_PLAN)
            {
                JSONObject json = new JSONObject();
                json.put("planId", packIns.getPrice());
                json.put("with", Lifeins.translate(packIns, content));

                return serviceMgr.req("lifeins", "plan/perform.json", json);
            }
            else if (packIns.getPriceType() == PackIns.PRICE_LIFE)
            {
                JSONObject json = new JSONObject();
                json.put("scriptId", packIns.getPrice());
                json.put("with", Lifeins.translate(packIns, content));

                return serviceMgr.req("lifeins", "perform.json", json);
            }
            else if (packIns.getPriceType() == PackIns.PRICE_PRODUCT)
            {
                JSONObject json = new JSONObject();
                json.put("productId", packIns.getPrice());
                json.put("with", Lifeins.translate(packIns, content));

                return serviceMgr.req("lifeins", "perform.json", json);
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
            pack = saleSrv.getPack(Common.toLong(vals.get("packId")));
        else if (vals.containsKey("productId"))
            pack = saleSrv.getPack(Common.toLong(vals.get("productId")));
        else if (vals.containsKey("packCode"))
            pack = saleSrv.getPack((String)vals.get("packCode"));

        return pack;
    }
}
