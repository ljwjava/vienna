package lerrain.service.order;

import com.alibaba.fastjson.JSONObject;
import lerrain.tool.Common;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@Controller
public class OrderController
{
    @Autowired
    OrderService orderSrv;

    @RequestMapping("/create.json")
    @ResponseBody
    @CrossOrigin
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
    @CrossOrigin
    public JSONObject restore(@RequestBody JSONObject p)
    {
        return restore(p.getLong("orderId"));
    }

    @RequestMapping({"/restore"})
    @ResponseBody
    @CrossOrigin
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
    @CrossOrigin
    public JSONObject list(@RequestBody JSONObject p)
    {
        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", null);

        return res;
    }

    @RequestMapping({"/view.json"})
    @ResponseBody
    @CrossOrigin
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
    @CrossOrigin
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
    @CrossOrigin
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
            order.setDetail(p.getJSONObject("detail"));
        if (p.containsKey("factors"))
            order.setFactors(p.getJSONObject("factors"));

        if (p.containsKey("pay"))
            order.setPay(p.getIntValue("pay"));
//        if (p.containsKey("status"))
//            order.setStatus(p.getIntValue("status"));
    }
}
