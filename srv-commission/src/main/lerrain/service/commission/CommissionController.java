package lerrain.service.commission;

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
public class CommissionController
{
    @Autowired CommissionService cs;

    /*@RequestMapping("/health")
    @ResponseBody
    @CrossOrigin
    public String health()
    {
        return "success";
    }*/

    @RequestMapping("/payoff.json")
    @ResponseBody
    @CrossOrigin
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
    @CrossOrigin
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

}
