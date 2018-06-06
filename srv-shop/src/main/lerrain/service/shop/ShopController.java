package lerrain.service.shop;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
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

    @RequestMapping("/commoditys.json")
    @ResponseBody
    public JSONObject commoditys(@RequestBody JSONObject p)
    {
        int currentPage = Common.intOf(p.get("currentPage"), 1);
        int num = Common.intOf(p.get("pageSize"), 10);
        int from =  num*(currentPage - 1);

        String search = p.getString("cdName");

        List<JSONObject> cdList = productSrv.commoditys(search, from, num);

        JSONArray titleList = new JSONArray();
        for (JSONObject obj : productSrv.types(search, from, num))
        {
            JSONArray list = new JSONArray();

            String type = obj.getString("tagCode");
            for (JSONObject listObj : cdList)
            {
                String typeCode = listObj.getString("type");
                if(StringUtils.equals(type,typeCode)){
                    list.add(listObj);
                }
            }
            obj.put("list",list);
            titleList.add(obj);
        }
        JSONObject r = new JSONObject();
        r.put("titleList", titleList);
        JSONObject page = new JSONObject();
        page.put("total",productSrv.count(null));
        page.put("pageSize", num);
        page.put("current", currentPage);
        r.put("pagination", page);

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", r);

        return res;
    }

}
