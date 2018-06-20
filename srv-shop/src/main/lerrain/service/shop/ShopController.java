package lerrain.service.shop;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import lerrain.tool.Common;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

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

    @RequestMapping("/commodityTypes.json")
    @ResponseBody
    public JSONObject commodityTypes(@RequestBody JSONObject p)
    {
        int currentPage = Common.intOf(p.get("currentPage"), 1);
        int num = Common.intOf(p.get("pageSize"), 20);
        int from =  num*(currentPage - 1);
        Long userId = p.getLong("userId");
        String name = p.getString("cdName");

        List<JSONObject> typeList = productSrv.types(name,from,num);
        List<JSONObject> titleList = Lists.newArrayList();
        JSONObject json = new JSONObject();
        json.put("tagId","1");
        json.put("tagCode","all");
        json.put("tagName","全部");
        json.put("prdNumbers",productSrv.count(userId, name, null));
        titleList.add(json);
        titleList.addAll(typeList);

        JSONObject r = new JSONObject();
        r.put("titleList", titleList);
        JSONObject page = new JSONObject();
        page.put("total",productSrv.countType(name));
        page.put("pageSize", num);
        page.put("current", currentPage);
        r.put("pagination", page);

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", r);

        return res;
    }

    @RequestMapping("/commoditys.json")
    @ResponseBody
    public JSONObject commoditys(@RequestBody JSONObject p)
    {
        int currentPage = Common.intOf(p.get("currentPage"), 1);
        int num = Common.intOf(p.get("pageSize"), 10);
        int from =  num*(currentPage - 1);

        Long userId = p.getLong("userId");
        String name = p.getString("cdName");
        String type = StringUtils.equals("all",p.getString("cdType"))?"":p.getString("cdType");

        List<JSONObject> cdList = productSrv.commoditys(userId, name, type, from, num);

        JSONObject r = new JSONObject();
        r.put("list", cdList);
        JSONObject page = new JSONObject();
        page.put("total",productSrv.count(userId, name, type));
        page.put("pageSize", num);
        page.put("current", currentPage);
        r.put("pagination", page);

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", r);

        return res;
    }

    @RequestMapping("/rateTemplates.json")
    @ResponseBody
    public JSONObject rateTemplates(@RequestBody JSONObject p)
    {
        int currentPage = Common.intOf(p.get("currentPage"), 1);
        int num = Common.intOf(p.get("pageSize"), 10);
        int from =  num*(currentPage - 1);

        Long userId = p.getLong("userId");
        String cdName = p.getString("cdName");

        List<JSONObject> rtList = productSrv.rateTemplates(userId, cdName, from, num);

        JSONObject r = new JSONObject();
        r.put("list", rtList);
        JSONObject page = new JSONObject();
        page.put("total",productSrv.countTemplate(userId, cdName));
        page.put("pageSize", num);
        page.put("current", currentPage);
        r.put("pagination", page);

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", r);

        return res;
    }

    @RequestMapping("/queryCommodityDetail.json")
    @ResponseBody
    public JSONObject queryCommodityDetail(@RequestBody JSONObject p)
    {
        Long wareId = p.getLong("wareId");

        Shop shop = productSrv.queryShopById(wareId);

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", JSON.toJSON(shop));

        return res;
    }

    @RequestMapping("/saveOrUpdateRateTemplate.json")
    @ResponseBody
    public JSONObject saveOrUpdateRateTemplate(@RequestBody JSONObject p)
    {
        RateTemplate rt = JSON.parseObject(p.toJSONString(),RateTemplate.class);
        Long tempId = productSrv.saveOrUpdateRateTemplate(rt);
        if(null == rt.getId()) {
            rt.setId(tempId);
        }
        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", JSON.toJSON(rt));

        return res;
    }

}
