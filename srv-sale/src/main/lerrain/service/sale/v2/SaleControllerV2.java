package lerrain.service.sale.v2;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lerrain.service.common.Log;
import lerrain.service.common.ServiceMgr;
import lerrain.service.sale.*;
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
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/v2")
public class SaleControllerV2
{
    @Autowired
    SaleService saleSrv;

    @Autowired
    ServiceMgr serviceMgr;

    @Value("${env}")
    String srvEnv;

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
//            p.put("summary", pack.getShow());
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

        // id
        // code
        // name
        // wareId
        // wareCode
        // type
        // applyMode
        // wareName
        // vendor
        // show
        // form
        // extra






        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", content);

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
