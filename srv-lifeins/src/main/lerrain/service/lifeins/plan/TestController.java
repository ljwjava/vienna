package lerrain.service.lifeins.plan;

import com.alibaba.fastjson.JSONObject;
import lerrain.project.insurance.plan.Plan;
import lerrain.service.lifeins.Customer;
import lerrain.service.lifeins.LifeinsService;
import lerrain.service.lifeins.LifeinsShow;
import lerrain.service.lifeins.format.FGraphFilter;
import lerrain.tool.Common;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TestController
{
    @Autowired
    PlanService ls;

    @Autowired
    LifeinsService lifeins;

    @RequestMapping("/plan/test1.json")
    @ResponseBody
    public JSONObject test1()
    {
        Customer app = new Customer();
        app.setAge(30);
        app.setGenderCode(Customer.GENDER_MALE);

        Plan plan = new Plan(app, app);
        plan.newCommodity(lifeins.getProduct("HQL00101"));
        plan.newCommodity(lifeins.getProduct("HXH00001"));

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", LifeinsShow.formatFGraph(plan));

        return res;
    }

    @RequestMapping("/plan/test2.json")
    @ResponseBody
    public JSONObject test2()
    {
        Customer app = new Customer();
        app.setAge(30);
        app.setGenderCode(Customer.GENDER_MALE);

        Plan plan = new Plan(app, app);
        plan.newCommodity(lifeins.getProduct("YGL00001"));
        plan.newCommodity(lifeins.getProduct("HQL00101"));
        plan.newCommodity(lifeins.getProduct("HXH00001"));

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", LifeinsShow.formatCsv(plan));

        return res;
    }
}
