package lerrain.service.lifeins;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lerrain.project.insurance.plan.Commodity;
import lerrain.project.insurance.plan.Plan;
import lerrain.tool.Common;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class FormatController
{
    @Autowired
    PlanService ls;

    @RequestMapping("/plan/format.json")
    @ResponseBody
    public JSONObject format(@RequestBody JSONObject p)
    {
        String planId = (String) p.get("planId");
        if (Common.isEmpty(planId))
            throw new RuntimeException("缺少planId");

        Plan plan = ls.getPlan(planId);
        JSONObject content = new JSONObject();

        for (String style : p.getString("style").split(","))
        {
            if ("radarGraph".equals(style))
                content.put(style, LifeinsShow.formatRadarGraph(plan));
            else if ("valChart".equals(style))
                content.put(style, LifeinsShow.formatValChart(plan));
        }

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", content);

        return res;
    }


    @RequestMapping("/plan/show_list.json")
    @ResponseBody
    public JSONObject findShow(@RequestBody JSONObject p)
    {
        String planId = (String) p.get("planId");
        if (Common.isEmpty(planId))
            throw new RuntimeException("缺少planId");

        Plan plan = ls.getPlan(planId);
        String type = (String) p.get("type");
        String[] format = null;

        if ("table".equals(type))
            format = new String[]{"benefit_table"};
        else if ("coverage".equals(type) || "liability".equals(type))
            format = new String[]{"liability", "coverage"};
        else if ("chart".equals(type))
            format = new String[]{"benefit_chart"};

        if (format == null)
            throw new RuntimeException("type is not exists");

        JSONArray r = new JSONArray();
        for (int j = 0; j < format.length; j++)
        {
            if (plan.hasFormat(format[j]))
            {
                r.add(-1);
                break;
            }
        }

        for (int i = 0; i < plan.size(); i++)
        {
            Commodity c = plan.getCommodity(i);
            for (int j = 0; j < format.length; j++)
            {
                if (c.hasFormat(format[j]))
                {
                    r.add(i);
                    break;
                }
                else if (c.getChildren() != null)
                {
                    for (Commodity child : (List<Commodity>)c.getChildren().toList())
                    {
                        if (child.hasFormat(format[j]))
                        {
                            r.add(i);
                            break;
                        }
                    }
                }
            }
        }

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", r);

        return res;
    }

    @RequestMapping("/plan/show.json")
    @ResponseBody
    public JSONObject show(@RequestBody JSONObject p)
    {
        String planId = (String) p.get("planId");
        if (Common.isEmpty(planId))
            throw new RuntimeException("缺少planId");

        int productIndex = Common.intOf(p.get("index"), 0);

        Plan plan = ls.getPlan(planId);
        Commodity c = plan.getCommodity(productIndex);

        JSONObject res = new JSONObject();
        res.put("result", "success");

        String type = (String) p.get("type");
        if ("table".equals(type))
        {
            boolean fold = Common.boolOf(p.get("fold"), false);

            JSONObject show = new JSONObject();
            if (c.hasFormat("benefit_table"))
            {
                JSONArray tables = LifeinsShow.formatTable(c, fold);
                show.put(c.getProduct().getName(), tables);
            }
            else if (c.getChildren() != null)
            {
                for (Commodity child : (List<Commodity>)c.getChildren().toList())
                {
                    if (child.hasFormat("benefit_table"))
                        show.put(child.getProduct().getName(), LifeinsShow.formatTable(child, fold));
                }
            }

            if (show.isEmpty())
            {
                res.put("result", "fail");
                res.put("reason", String.format("未找到对应表格：planId=%s,productIndex=%d", planId, productIndex));
            }
            else
            {
                res.put("content", show);
            }
        }
        else if ("chart".equals(type))
        {
            JSONObject show = new JSONObject();
            if (c.hasFormat("benefit_chart"))
            {
                show.put(c.getProduct().getName(), LifeinsShow.formatChart(c));
            }
            else if (c.getChildren() != null)
            {
                for (Commodity child : (List<Commodity>)c.getChildren().toList())
                {
                    if (child.hasFormat("benefit_chart"))
                        show.put(child.getProduct().getName(), LifeinsShow.formatChart(child));
                }
            }

            if (show.isEmpty())
            {
                res.put("result", "fail");
                res.put("reason", String.format("未找到对应图表：planId=%s,productIndex=%d", planId, productIndex));
            }
            else
            {
                res.put("content", show);
            }
        }
        else if ("coverage".equals(type) || "liability".equals(type))
        {
            JSONObject coverage = new JSONObject();
            if (c.hasFormat("liability"))
            {
                coverage.put(c.getProduct().getName(), LifeinsShow.formatLiability(c));
            }
            else if (c.hasFormat("coverage"))
            {
                coverage.put(c.getProduct().getName(), LifeinsShow.formatCoverage(c));
            }
            else if (c.getChildren() != null)
            {
                for (Commodity child : (List<Commodity>)c.getChildren().toList())
                {
                    if (child.hasFormat("liability"))
                        coverage.put(child.getProduct().getName(), LifeinsShow.formatLiability(child));
                    else if (child.hasFormat("coverage"))
                        coverage.put(child.getProduct().getName(), LifeinsShow.formatCoverage(child));
                }
            }
            res.put("content", coverage);
        }

        return res;
    }

}
