package lerrain.service.product.fee;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lerrain.service.common.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class CustFeeController
{
    @Autowired
    CustFeeService ccs;

    @RequestMapping("/fee/rate/seek.json")
    @ResponseBody
    public JSONObject getFeeDef(@RequestBody JSONObject c)
    {
        Long schemeId = c.getLong("schemeId");
        Long productId = c.getLong("productId");
        Date time = c.getDate("time");
        if (time == null)
            time = new Date();
        JSONObject seek = c.getJSONObject("factors");

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", ccs.getFeeDefine(schemeId, productId, time, seek));

        return res;
    }

    @RequestMapping("/fee/rate/query.json")
    @ResponseBody
    public JSONObject listFeeDef(@RequestBody JSONObject c)
    {
        Long schemeId = c.getLong("schemeId");
        Long productId = c.getLong("productId");

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", ccs.listFeeDefine(schemeId, productId));

        return res;
    }

    /**
     * @param c
     *     wareId productId 二选一
     *
     * @return = [{
     *     pay: {code: ?, text: ?}
     *     insure: {code: ?, text: ?}
     * }, ...]
     *
     * 因子的数量不确定，目前常见的是
     * 1 pay+insure，缴费期及保障期
     * 2 pay，仅看缴费期，长险通常是这种
     * 3 全空，短险通常是这种，表示该产品就一种佣金比例，数组也只有1的长度，此时返回值是[{}]
     */
    @RequestMapping("/fee/rate/factors.json")
    @ResponseBody
    public JSONObject factors(@RequestBody JSONObject c)
    {
        Object r = null;

        if (c.containsKey("wares"))
        {
            JSONArray wares = c.getJSONArray("ware");
            JSONObject r1 = new JSONObject();
            for (int i = 0; i < wares.size(); i++)
            {
                Long wareId = wares.getLong(i);
                r1.put(wareId.toString(), ccs.listFeeDefineFactorsByWareId(wareId));
            }
            r = r1;
        }
        else if (c.containsKey("products"))
        {
            JSONArray products = c.getJSONArray("product");
            JSONObject r1 = new JSONObject();
            for (int i = 0; i < products.size(); i++)
            {
                Long productId = products.getLong(i);
                r1.put(productId.toString(), ccs.listFeeDefineFactors(productId));
            }
            r = r1;
        }
        else if (c.containsKey("product"))
        {
            r = ccs.listFeeDefineFactors(c.getLong("product"));
        }

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", r);

        return res;
    }

    /**
     * @param c = {
     *     schemeId: Long
     *     productId: Long
     *     begin: Time,
     *     end: Time,
     *     content: {
     *         factors: {
     *             ... //key, value为factors里面获取的
     *         }
     *         rate: [ ... ], //每年的比例
     *         freeze: Integer,
     *         unit: 1-比例 2-固定数额 3-百分比
     *     }
     * }
     *
     * OR
     *
     * @param c = {
     *     schemeId: Long
     *     list: [{
     *         productId: Long
     *         begin: Time,
     *         end: Time,
     *         content: {
     *             factors: {
     *                 ...
     *             }
     *             rate: [ ... ],
     *             freeze: Integer,
     *             unit: 1-比例 2-固定数额 3-百分比
     *         }
     *     }, ...]
     * }
     *
     * @return
     */
    @RequestMapping("/fee/rate/save.json")
    @ResponseBody
    public JSONObject save(@RequestBody JSONObject c)
    {
        Long schemeId = c.getLong("schemeId");

        if (c.containsKey("list"))
        {
            JSONArray all = c.getJSONArray("list");

            for (int i = 0; i < all.size(); i++)
                saveFeeDefine(schemeId, all.getJSONObject(i));
        }
        else
        {
            saveFeeDefine(schemeId, c);
        }

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", schemeId);

        return res;
    }

    private void saveFeeDefine(Long schemeId, JSONObject c)
    {
        Log.info(c);

        Long productId = c.getLong("productId");

        List<CustFeeDefine> list = new ArrayList<>();
        JSONArray content = c.getJSONArray("content");

        Date begin = c.getDate("begin");
        Date end = c.getDate("end");

        for (int i = 0; i < content.size(); i++)
        {
            CustFeeDefine fd = content.getObject(i, CustFeeDefine.class);
            fd.setProductId(productId);
            fd.setSchemeId(schemeId);
            list.add(fd);
        }

        ccs.saveFeeDefine(schemeId, productId, begin, end, list);
    }
}
