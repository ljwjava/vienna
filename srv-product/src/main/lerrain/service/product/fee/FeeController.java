package lerrain.service.product.fee;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lerrain.service.common.Log;
import lerrain.tool.Common;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
public class FeeController
{
    @Autowired
    FeeService cs;

    @RequestMapping("/fee/retry.json")
    @ResponseBody
    public JSONObject retry(@RequestBody JSONArray ids)
    {
        List<Long> idlist = new ArrayList<Long>();
        for (int i = 0; i < ids.size(); i++)
            idlist.add(ids.getLong(i));

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", cs.pay(idlist));

        return res;
    }

    @RequestMapping("/fee/pay.json")
    @ResponseBody
    public JSONObject pay(@RequestBody JSONObject c)
    {
        Long platformId = c.getLong("platformId");
        //Long productId = c.getLong("productId");
        Long vendorId = c.getLong("vendorId");
        String bizNo = c.getString("bizNo");

        int fail = cs.payAll(platformId, vendorId, bizNo);

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", fail);

        return res;
    }


    @RequestMapping("/fee/bill.json")
    @ResponseBody
    public JSONObject bill(@RequestBody JSONArray list)
    {
        //这里要加判断，一张保单只允许bill一次

        List<Fee> bills = new ArrayList<>();
        for (int i = 0; i < list.size(); i++)
        {
            JSONObject c = list.getJSONObject(i);

            Fee r = new Fee();
            r.id = Common.toLong(c.get("id")); //
            r.platformId = Common.toLong(c.get("platformId"));
            r.payer = Common.toLong(c.get("payer"));
            r.drawer = Common.toLong(c.get("drawer"));
            r.productId = Common.trimStringOf(c.get("productId"));
            r.vendorId = Common.toLong(c.get("vendorId"));
            r.bizType = Common.intOf(c.get("bizType"), 0);
            r.bizId = Common.toLong(c.get("bizId"));
            r.bizNo = Common.trimStringOf(c.get("bizNo"));
            r.memo = Common.trimStringOf(c.get("memo"));
            r.extra = c.getJSONObject("extra");
            r.amount = Common.doubleOf(c.get("amount"), 0);
            r.auto = Common.boolOf(c.get("auto"), false);
            r.estimate = Common.dateOf(c.get("estimate"));
            r.type = Common.intOf(c.get("type"), 0);
            r.unit = Common.intOf(c.get("unit"), 1);
            r.freeze = Common.intOf(c.get("freeze"), 0);
            r.status = Common.intOf(c.get("status"), 9); //
            r.payTime = null;
            r.createTime = new Date();

            bills.add(r);
        }

        cs.bill(bills);

        JSONObject res = new JSONObject();
        res.put("result", "success");

        return res;
    }

    @RequestMapping("/fee/query_rate.json")
    @ResponseBody
    public JSONObject listFeeDef(@RequestBody JSONObject c)
    {
        Long platformId = c.getLong("platformId");
        Long productId = c.getLong("productId");

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", cs.listFeeDefine(platformId, productId));

        return res;
    }

    @RequestMapping("/fee/save_rate.json")
    @ResponseBody
    public JSONObject saveRate(@RequestBody JSONObject c)
    {
        Log.info(c);

        Long platformId = c.getLong("platformId");
        Long productId = c.getLong("productId");

        List<FeeDefine> list = new ArrayList<>();
        JSONArray detail = c.getJSONArray("detail");

        for (int i=0;i<detail.size();i++)
        {
            FeeDefine fd = detail.getObject(i, FeeDefine.class);
            fd.setProductId(productId);
            fd.setPlatformId(platformId);
            list.add(fd);
        }

        cs.saveFeeDefine(platformId, productId, list);

        JSONObject res = new JSONObject();
        res.put("result", "success");

        return res;
    }

    @RequestMapping("/fee/find.json")
    @ResponseBody
    public JSONObject find(@RequestBody JSONObject c)
    {
        //条件组1
        Long platformId = c.getLong("platformId");
//        Long productId = c.getLong("productId");
        Long vendorId = c.getLong("vendorId");
        String bizNo = c.getString("bizNo");

        //条件组2
        Integer bizType = c.getInteger("bizType");
        Long bizId = c.getLong("bizId");

        Object r = null;
        if (bizType != null && bizId != null)
            r = cs.findFee(bizType, bizId);
        else if (platformId != null && vendorId != null && bizNo != null)
            r = cs.findFee(platformId, vendorId, bizNo);

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", r);

        return res;
    }

    @RequestMapping("/fee/rate.json")
    @ResponseBody
    public JSONObject fee(@RequestBody JSONObject c)
    {
        Long platformId = c.getLong("platformId");
        Long productId = c.getLong("productId");
        //Long vendorId = c.getLong("vendorId");

        Map factors = c.getJSONObject("factors");

        Date time = c.getDate("time");
        if (time == null)
            time = new Date();

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", cs.getFeeDefine(platformId, productId, factors, time));

        return res;
    }

}
