package lerrain.service.order;

import com.alibaba.fastjson.JSONObject;
import lerrain.tool.Common;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Map;

@Controller
public class OrderController
{
    @Autowired
    OrderService orderSrv;

    /*@RequestMapping("/health")
    @ResponseBody
    @CrossOrigin
    public String health()
    {
        return "success";
    }*/

    @RequestMapping("/create.json")
    @ResponseBody
    public JSONObject create(@RequestBody JSONObject p)
    {
        Order order = orderSrv.newOrder();
        fill(order, p);

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", order);

        return res;
    }

    @RequestMapping({"/restore.json"})
    @ResponseBody
    public JSONObject restore(@RequestBody JSONObject p)
    {
        return restore(p.getLong("orderId"));
    }

    @RequestMapping({"/restore"})
    @ResponseBody
    public JSONObject restore(@RequestParam("orderId") Long orderId)
    {
        if (orderId == null)
            throw new RuntimeException("no orderId");

        Order order = orderSrv.getOrder(orderId);
        synchronized (order)
        {
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

    @RequestMapping({"/list.json"})
    @ResponseBody
    public JSONObject list(@RequestBody JSONObject p)
    {
        int type = p.getIntValue("type");
        Long owner = p.getLong("owner");

        int from = Common.intOf(p.get("from"), 0);
        int num = Common.intOf(p.get("num"), 10);

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", orderSrv.query(type, owner, from, num));

        return res;
    }

    @RequestMapping({"/view.json"})
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

    @RequestMapping({"/save.json"})
    @ResponseBody
    public JSONObject save(@RequestBody JSONObject p)
    {
        Long orderId = p.getLong("orderId");
        if (orderId == null)
            orderId = p.getLong("id");
        if (orderId == null)
            throw new RuntimeException("no orderId");

        Order order = orderSrv.getOrder(orderId);
        if (order.getStatus() != 1)
            throw new RuntimeException("订单<"+orderId+">处理失败：只有未提交或退回的订单才可以修改");

        synchronized (order)
        {
            fill(order, p);
        }

        orderSrv.saveOrder(order);

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", order);

        return res;
    }

    @RequestMapping({"/update.json"})
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
            if (p.containsKey("bizNo"))
                order.setBizNo(p.getString("bizNo"));
            if (p.containsKey("bizMsg"))
                order.setBizMsg(p.getString("bizMsg"));
            if (p.containsKey("extra"))
                order.setExtra(fill(order.getExtra(), p.getJSONObject("extra")));

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

    private void fill(Order order, JSONObject p)
    {
        order.setModifyTime(new Date());

        if (p.containsKey("type"))
            order.setType(p.getIntValue("type"));
        if (p.containsKey("price"))
            order.setPrice(Common.decimalOf(p.get("price")));
        if (p.containsKey("productId"))
            order.setProductId(p.getString("productId"));
        if (p.containsKey("productName"))
            order.setProductName(p.getString("productName"));
        if (p.containsKey("productType"))
            order.setProductType(p.getIntValue("productType"));
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
            order.setDetail(fill(order.getDetail(), p.getJSONObject("detail")));
        if (p.containsKey("extra"))
            order.setExtra(fill(order.getExtra(), p.getJSONObject("extra")));

//        if (p.containsKey("pay"))
//            order.setPay(p.getIntValue("pay"));
//        if (p.containsKey("status"))
//            order.setStatus(p.getIntValue("status"));
    }

    private Map fill(Map m1, Map m2)
    {
        if (m1 == null)
            return m2;
        if (m2 == null)
            return m1;

        m1.putAll(m2);
        return m1;
    }
}
