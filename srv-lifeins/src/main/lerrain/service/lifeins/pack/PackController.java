package lerrain.service.lifeins.pack;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lerrain.service.lifeins.CmsDefine;
import lerrain.service.lifeins.CmsService;
import lerrain.service.lifeins.LifeinsService;
import lerrain.service.lifeins.plan.PlanService;
import lerrain.tool.Common;
import lerrain.tool.formula.Formula;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class PackController
{
    @Autowired
    PackService packSrv;

    @Autowired
    PlanService planSrv;

    @Autowired
    LifeinsService lifeins;

    @Autowired
    CmsService cmsSrv;

    @RequestMapping({"/reset"})
    @ResponseBody
    public String reset()
    {
        packSrv.reset();

        return "success";
    }

    @RequestMapping({ "/pack/perform.json" })
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
            r = packSrv.premium(packIns, content);
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
        else if ("fee".equals(opt))
        {
            r = packSrv.fee(packIns, content);
        }
        else if ("plan".equals(opt))
        {
            r = packSrv.detail(packIns, content);
        }
        else if ("quest".equals(opt))
        {
            r = packSrv.quest(packIns, content);
        }
        else
        {
            r = packSrv.perform(opt, packIns, content);
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
//        else if (vals.containsKey("product"))
//            pack = packSrv.getPack((String)vals.get("product"));
//        else if (vals.containsKey("productCode"))
//            pack = packSrv.getPack((String)vals.get("productCode"));

        return pack;
    }

    @RequestMapping({ "/pack/view.json" })
    @ResponseBody
    public JSONObject view(@RequestBody JSONObject json)
    {
        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", PackUtil.toJson(getPack(json)));

        return res;
    }
}
