package lerrain.service.commission;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PostConstruct;
import java.util.*;

@Controller
public class CommissionController
{
    @Autowired
    CommissionService cs;

    @Autowired
    CmsDefineService cmsDefSrv;

    @PostConstruct
    @RequestMapping("/reset")
    @ResponseBody
    public String reset()
    {
        cmsDefSrv.reset();
        return "success";
    }

    @RequestMapping("/payoff.json")
    @ResponseBody
    public JSONObject payoff(@RequestBody JSONArray list)
    {
        for (int i=0;i<list.size();i++)
        {
            Map map = list.getJSONObject(i);
            Commission commission = Commission.commissionOf(map);

            if (commission.getAmount() > 0)
                cs.store(commission);
        }

        JSONObject res = new JSONObject();
        res.put("result", "success");

        return res;
    }

    @RequestMapping("/retry.json")
    @ResponseBody
    public JSONObject retry(@RequestBody JSONArray ids)
    {
        List<Long> idlist = new ArrayList<Long>();
        for (int i=0;i<ids.size();i++)
        {
            Long id = ids.getLong(i);
            idlist.add(id);
        }
        cs.retry(idlist);

        JSONObject res = new JSONObject();
        res.put("result", "success");

        return res;
    }

    @RequestMapping("/fee.json")
    @ResponseBody
    public JSONObject fee(@RequestBody JSONArray l)
    {
        JSONArray r = new JSONArray();

        for (int j = 0; j < l.size(); j++)
        {
            JSONObject c = l.getJSONObject(j);
            Long platformId = c.getLong("platformId");
            String product = c.getString("product");
            String group = c.getString("group");
            String payFreq = c.getString("payFreq");
            String payPeriod = c.getString("payPeriod");

            double[] rate = new double[5];

            List<CmsDefine> list = cmsDefSrv.getCommissionRate(platformId, group, product, payFreq, payPeriod);
            if (list != null) for (CmsDefine cd : list)
            {
                double[] r1 = cd.getSelfRate();
                for (int i = 0; i < r1.length; i++)
                    rate[i] += r1[i];
            }

            r.add(rate);
        }

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", r);

        return res;
    }

    @RequestMapping("/pay_all.json")
    @ResponseBody
    public JSONObject payAll(@RequestBody JSONObject c)
    {
        Date now = new Date();
        Calendar calendar = Calendar.getInstance();

        Long platformId = c.getLong("platformId");
        String product = c.getString("product");
        String group = c.getString("group");
        String payFreq = c.getString("payFreq");
        String payPeriod = c.getString("payPeriod");

        double bonus = c.getDouble("bonus");
        double amount = c.getDouble("amount");

        Long userId = c.getLong("userId");
        Long parentId = c.getLong("parentId");
        String bizNo = c.getString("bizNo");
        String memo = c.getString("memo");
        JSONObject extra = c.getJSONObject("extra");

        int freeze = 3;

        List<CmsDefine> list = cmsDefSrv.getCommissionRate(platformId, group, product, payFreq, payPeriod);
        for (CmsDefine cd : list)
        {
            double[] f = cd.self;
            for (int i = 0; i < f.length; i++)
            {
                if (f[i] > 0)
                {
                    calendar.setTime(now);
                    calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) + i);

                    Commission r = new Commission();
                    r.userId = userId;
                    r.platformId = platformId;
                    r.productId = product;
                    r.bizNo = bizNo;
                    r.amount = amount * f[i];
                    r.auto = i == 0;
                    r.estimate = calendar.getTime();
                    r.type = 1;
                    r.unit = 1;
                    r.freeze = cd.freeze;
                    r.extra = extra == null ? null : extra.toString();
                    r.memo = memo;

                    if (r.freeze > freeze)
                        freeze = r.freeze;

                    if (r.getAmount() > 0)
                        cs.store(r);
                }
            }

            if (parentId != null)
            {
                f = cd.parent;
                for (int i = 0; i < f.length; i++)
                {
                    if (f[i] > 0)
                    {
                        calendar.setTime(now);
                        calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) + i);

                        Commission r = new Commission();
                        r.userId = parentId;
                        r.fromUserId = userId;
                        r.platformId = platformId;
                        r.productId = product;
                        r.bizNo = bizNo;
                        r.amount = amount * f[i];
                        r.auto = i == 0;
                        r.estimate = calendar.getTime();
                        r.type = 2;
                        r.unit = 1;
                        r.freeze = cd.freeze;
                        r.extra = extra == null ? null : extra.toString();
                        r.memo = memo;

                        if (r.getAmount() > 0)
                            cs.store(r);
                    }
                }
            }
        }

        if (bonus > 0)
        {
            Commission r = new Commission();
            r.userId = userId;
            r.platformId = platformId;
            r.productId = product;
            r.bizNo = bizNo;
            r.amount = amount * bonus;
            r.auto = true;
            r.estimate = now;
            r.type = 3;
            r.unit = 1;
            r.freeze = freeze;
            r.extra = extra == null ? null : extra.toString();
            r.memo = memo;

            if (r.getAmount() > 0)
                cs.store(r);
        }

        JSONObject res = new JSONObject();
        res.put("result", "success");

        return res;
    }
}
