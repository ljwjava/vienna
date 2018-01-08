package lerrain.service.sale.manage;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lerrain.service.sale.SaleService;
import lerrain.service.sale.Ware;
import lerrain.tool.Common;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class ManageController
{
    @Autowired
    SaleService wareSrv;

    @Autowired
    ManageService manageSrv;

    @RequestMapping("product/edit.json")
    @ResponseBody
    public JSONObject edit(@RequestBody JSONObject req)
    {
        Product prd = manageSrv.loadProduct(req.getLong("productId"));

        JSONObject r = new JSONObject();

        JSONObject base = new JSONObject();
        base.put("code", "base");
        base.put("name", "基础信息");

        List<FormField> list = new ArrayList();
        list.add(FormField.fieldOf("id", "产品ID", "text", null, prd.getId()));
        list.add(FormField.fieldOf("code", "产品CODE", "text", null, prd.getCode()));
        list.add(FormField.fieldOf("name", "名称", "text", null, prd.getName()));
        list.add(FormField.fieldOf("company", "公司", "select", manageSrv.getCompanyList(), prd.getCompanyId()));
        list.add(FormField.fieldOf("type", "类型", "select", JSON.parseArray("[['1','个险条款'],['2','个险组合'],['3','团险组合']]"), "2"));
        base.put("form", list);

        r.put("base", base);

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", r);

        return res;
    }

    @RequestMapping("product/list.json")
    @ResponseBody
    public JSONObject list(@RequestBody JSONObject json)
    {
        String search = json.getString("search");
        int from = Common.intOf(json.get("from"), 0);
        int number = Common.intOf(json.get("number"), -1);

        JSONObject r = new JSONObject();
        r.put("list", manageSrv.list(search, from, number));
        r.put("total", manageSrv.count(search));

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", r);

        return res;
    }
}
