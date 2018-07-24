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
import java.util.UUID;

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
//        Long userId = p.getLong("userId");
//        String name = p.getString("commodityName");
        Shop contion = JSONObject.parseObject(p.toJSONString(),Shop.class);

        List<JSONObject> typeList = productSrv.types(contion,from,num);
        List<JSONObject> titleList = Lists.newArrayList();
        JSONObject json = new JSONObject();
        json.put("tagId","1");
        json.put("tagCode","all");
        json.put("tagName","全部");
        json.put("prdNumbers",productSrv.count(contion));
        titleList.add(json);
        titleList.addAll(typeList);

        JSONObject r = new JSONObject();
        r.put("titleList", titleList);
        JSONObject page = new JSONObject();
        page.put("total",productSrv.countType(contion));
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

//        Long userId = p.getLong("userId");
//        String name = p.getString("commodityName");
//        String type = StringUtils.equals("all",p.getString("commodityTypeCode"))?"":p.getString("commodityTypeCode");

        Shop contion = JSONObject.parseObject(p.toJSONString(),Shop.class);

        List<Shop> cdList = productSrv.commoditys(contion, from, num);

        JSONObject r = new JSONObject();
        r.put("list", cdList);
        JSONObject page = new JSONObject();
        page.put("total",productSrv.count(contion));
        page.put("pageSize", num);
        page.put("current", currentPage);
        r.put("pagination", page);

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", r);

        return res;
    }

    //查询用户创建的模板
    @RequestMapping("/rateTemplates.json")
    @ResponseBody
    public JSONObject rateTemplates(@RequestBody JSONObject p)
    {
        int currentPage = Common.intOf(p.get("currentPage"), 1);
        int num = Common.intOf(p.get("pageSize"), 10);
        int from =  num*(currentPage - 1);

//        Long userId = p.getLong("userId");
//        String cdName = p.getString("cdName");

        RateTemp contion = JSONObject.parseObject(p.toJSONString(),RateTemp.class);

        List<JSONObject> rtList = productSrv.rateTemplates(contion, from, num);

        JSONObject r = new JSONObject();
        r.put("list", rtList);
        JSONObject page = new JSONObject();
        page.put("total",productSrv.countTemplate(contion));
        page.put("pageSize", num);
        page.put("current", currentPage);
        r.put("pagination", page);

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", r);

        return res;
    }

    //查询用户已使用的模板
    @RequestMapping("/usedRateTemplates.json")
    @ResponseBody
    public JSONObject usedRateTemplates(@RequestBody JSONObject p)
    {
        int currentPage = Common.intOf(p.get("currentPage"), 1);
        int num = Common.intOf(p.get("pageSize"), 10);
        int from =  num*(currentPage - 1);

//        Long userId = p.getLong("userId");
//        String cdName = p.getString("cdName");

        RateTemp contion = JSONObject.parseObject(p.toJSONString(),RateTemp.class);

        List<JSONObject> rtList = productSrv.usedRateTemplates(contion, from, num);

        JSONObject r = new JSONObject();
        r.put("list", rtList);
        JSONObject page = new JSONObject();
        page.put("total",productSrv.countUsedTemplate(contion));
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
        JSONObject res = new JSONObject();
        RateTemp contion = JSON.parseObject(p.toJSONString(), RateTemp.class);
        RateTemp rt = productSrv.saveOrUpdateRateTemplate(contion);
        res.put("result", "success");
        res.put("content", JSON.toJSON(rt));

        return res;
    }

    @RequestMapping("/deleteRateTemplate.json")
    @ResponseBody
    public JSONObject deleteRateTemplate(@RequestBody JSONObject p)
    {
        JSONObject res = new JSONObject();
        RateTemp contion = JSON.parseObject(p.toJSONString(), RateTemp.class);
        RateTemp rt = productSrv.deleteRateTemplate(contion);
        res.put("result", "success");
        res.put("content", JSON.toJSON(rt));

        return res;
    }

    @RequestMapping("/batchOperateRateTemplate.json")
    @ResponseBody
    public JSONObject batchOperateRateTemplate(@RequestBody JSONObject p)
    {
        JSONObject res = new JSONObject();
        JSONObject content = new JSONObject();
        res.put("result", "success");
        JSONArray list = p.getJSONArray("list");
        Long userId = p.getLong("userId");
        if(null != list && list.size() > 0) {
            List<RateTemp> contions = JSON.parseArray(list.toJSONString(), RateTemp.class);
        	for(RateTemp r : contions) {
        		r.setUserId(userId);
        		r.setCreator(userId+"");
        		r.setModifier(userId+"");
        	}
            List<RateTemp> rts = productSrv.batchOperateRateTemplate(contions);
            content.put("content",JSON.toJSON(rts));
        }
        return res;
    }

    @RequestMapping("/saveOrUpdateQrcodeInfo.json")
    @ResponseBody
    public JSONObject saveOrUpdateQrcodeInfo(@RequestBody JSONObject p)
    {
        JSONObject res = new JSONObject();
        Qrcode contion = JSON.parseObject(p.toJSONString(), Qrcode.class);
        contion.setQrcodeUid(UUID.randomUUID().toString().trim().replaceAll("-", "")
                .substring(0, 14));
        Qrcode qr = productSrv.saveOrUpdateQrcodeInfo(contion);
        res.put("result", "success");
        res.put("content", JSON.toJSON(qr));

        return res;
    }

}
