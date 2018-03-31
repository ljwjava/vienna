package lerrain.service.order;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lerrain.tool.Common;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
public class OrderController
{
    @Autowired
    OrderService orderSrv;

    @RequestMapping("/create.json")
    @ResponseBody
    public JSONObject create(@RequestBody JSONObject p)
    {
        Order order = orderSrv.newOrder();
        fill(order, p, false);

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", order);

        return res;
    }

    @RequestMapping("/children.json")
    @ResponseBody
    public JSONObject children(@RequestBody JSONObject p)
    {
        Long orderId = p.getLong("orderId");

        if (Common.isEmpty(orderId))
            throw new RuntimeException("缺少orderId");

        JSONArray children = p.getJSONArray("children");
        if (children == null || children.isEmpty())
            throw new RuntimeException("children为空");

        Order order = orderSrv.getOrder(orderId);

        List<Order> c = new ArrayList<>();

        for (int i = 0; i < children.size(); i++)
        {
            Order child = orderSrv.newOrder();
            fill(child, children.getJSONObject(i), false);

            c.add(child);
        }

        orderSrv.setChildren(order, c);

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", order);

        return res;
    }

    @RequestMapping("/restore.json")
    @ResponseBody
    public JSONObject restore(@RequestBody JSONObject p)
    {
        return restore(p.getLong("orderId"));
    }

    @RequestMapping("/restore")
    @ResponseBody
    public JSONObject restore(@RequestParam("orderId") Long orderId)
    {
        if (orderId == null)
            throw new RuntimeException("no orderId");

        Order order = orderSrv.getOrder(orderId);
        synchronized (order)
        {
            order.setBizId(null);
            order.setBizNo(null);
            order.setBizMsg(null);
            order.setPay(1);
            order.setStatus(1);
        }

        orderSrv.update(order);

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", order);

        return res;
    }

    @RequestMapping("/list.json")
    @ResponseBody
    public JSONObject list(@RequestBody JSONObject p)
    {
        int type = p.getIntValue("type");
        Long owner = p.getLong("owner");
        Long platformId = p.getLong("platformId");

        Integer productType = p.getInteger("productType");

        int from = Common.intOf(p.get("from"), 0);
        int num = Common.intOf(p.get("num"), 10);

        JSONObject r = new JSONObject();
        r.put("list", orderSrv.list(type, from, num, platformId, productType, owner));
        r.put("total", orderSrv.count(type, platformId, productType, owner));

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", r);

        return res;
    }

    @RequestMapping("/query.json")
    @ResponseBody
    public JSONObject query(@RequestBody JSONObject p)
    {
        int type = p.getIntValue("type");
        Long owner = p.getLong("owner");
        Long platformId = p.getLong("platformId");

        Integer productType = p.getInteger("productType");

        int from = Common.intOf(p.get("from"), 0);
        int num = Common.intOf(p.get("num"), 10);

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", orderSrv.list(type, from, num, platformId, productType, owner));

        return res;
    }

    @RequestMapping("/view.json")
    @ResponseBody
    public JSONObject view(@RequestBody JSONObject p)
    {
        Long orderId = p.getLong("orderId");

        if (Common.isEmpty(orderId))
            throw new RuntimeException("缺少orderId");

        Order order = orderSrv.getOrder(orderId);

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", order);

        return res;
    }

    @RequestMapping("/reload.json")
    @ResponseBody
    public JSONObject reload(@RequestBody JSONObject p)
    {
        Long orderId = p.getLong("orderId");

        if (Common.isEmpty(orderId))
            throw new RuntimeException("缺少orderId");

        Order order = orderSrv.reloadOrder(orderId);

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", order);

        return res;
    }

    @RequestMapping("/replace.json")
    @ResponseBody
    public JSONObject replace(@RequestBody JSONObject p)
    {
        Long orderId = p.getLong("orderId");
        if (orderId == null)
            orderId = p.getLong("id");
        if (orderId == null)
            throw new RuntimeException("no orderId");

        Order order = orderSrv.getOrder(orderId);
        if (order.getStatus() != 1 && order.getStatus() != 4)
            throw new RuntimeException("订单<"+orderId+">处理失败：只有未提交或退回的订单才可以修改");

        synchronized (order)
        {
            fill(order, p, true);
        }

        orderSrv.saveOrder(order);

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", order);

        return res;
    }

    @RequestMapping("/save.json")
    @ResponseBody
    public JSONObject save(@RequestBody JSONObject p)
    {
        Long orderId = p.getLong("orderId");
        if (orderId == null)
            orderId = p.getLong("id");
        if (orderId == null)
            throw new RuntimeException("no orderId");

        Order order = orderSrv.getOrder(orderId);
        if (order.getStatus() != 1 && order.getStatus() != 4)
            throw new RuntimeException("订单<"+orderId+">处理失败：只有未提交或退回的订单才可以修改");

        synchronized (order)
        {
            fill(order, p, false);
        }

        orderSrv.saveOrder(order);

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", order);

        return res;
    }

    @RequestMapping("/update.json")
    @ResponseBody
    public JSONObject update(@RequestBody JSONObject p)
    {
        Long orderId = p.getLong("orderId");
        if (orderId == null)
            orderId = p.getLong("id");
        if (orderId == null)
            throw new RuntimeException("no orderId");

        int pay = Common.intOf(p.get("pay"), -1);
        int status = Common.intOf(p.get("status"), -1);

        Order order = orderSrv.getOrder(orderId);
        synchronized (order)
        {
            if (p.containsKey("bizId"))
                order.setBizId(p.getLong("bizId"));
            if (p.containsKey("applyNo"))
                order.setApplyNo(p.getString("applyNo"));
            if (p.containsKey("bizNo"))
                order.setBizNo(p.getString("bizNo"));
            if (p.containsKey("bizMsg"))
                order.setBizMsg(p.getString("bizMsg"));
            if (p.containsKey("extra"))
                order.setExtra(fill(order.getExtra(), p.getJSONObject("extra"), false));

            if (pay >= 0)
                order.setPay(pay);
            if (status >= 0)
                order.setStatus(status);
        }

        orderSrv.update(order);

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", order);

        return res;
    }

    private void fill(Order order, JSONObject p, boolean isReset)
    {
        order.setModifyTime(new Date());

        if (p.containsKey("type"))
            order.setType(p.getIntValue("type"));
        if (p.containsKey("price"))
            order.setPrice(Common.decimalOf(p.get("price")));
        if (p.containsKey("productId"))
            order.setProductId(p.getString("productId"));
        if (p.containsKey("productCode"))
            order.setProductCode(p.getString("productCode"));
        if (p.containsKey("productName"))
            order.setProductName(p.getString("productName"));
        if (p.containsKey("consumer"))
            order.setConsumer(p.getString("consumer"));
        if (p.containsKey("productType"))
            order.setProductType(p.getIntValue("productType"));
        if (p.containsKey("bizId"))
            order.setBizId(p.getLong("bizId"));
        if (p.containsKey("applyNo"))
            order.setApplyNo(p.getString("applyNo"));
        if (p.containsKey("bizNo"))
            order.setBizNo(p.getString("bizNo"));
        if (p.containsKey("bizMsg"))
            order.setBizMsg(p.getString("bizMsg"));
        if (p.containsKey("vendorId"))
            order.setVendorId(p.getLong("vendorId"));
        if (p.containsKey("platformId"))
            order.setPlatformId(p.getLong("platformId"));
        if (p.containsKey("owner"))
            order.setOwner(p.getString("owner"));

        if (p.containsKey("detail"))
            order.setDetail(fill(order.getDetail(), p.getJSONObject("detail"), isReset));
        if (p.containsKey("extra"))
            order.setExtra(fill(order.getExtra(), p.getJSONObject("extra"), isReset));

//        if (p.containsKey("pay"))
//            order.setPay(p.getIntValue("pay"));
//        if (p.containsKey("status"))
//            order.setStatus(p.getIntValue("status"));
    }

    private Map fill(Map m1, Map m2, boolean reset)
    {
        if (reset)
            return m2;

        if (m1 == null)
            return m2;
        if (m2 == null)
            return m1;

        m1.putAll(m2);

        return m1;
    }
}
