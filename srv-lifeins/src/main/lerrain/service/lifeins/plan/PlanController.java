package lerrain.service.lifeins.plan;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lerrain.project.insurance.plan.Commodity;
import lerrain.project.insurance.plan.Plan;
import lerrain.project.insurance.product.*;

import lerrain.tool.Common;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lerrain.service.lifeins.Customer;
import lerrain.service.lifeins.LifeinsService;
import lerrain.service.lifeins.LifeinsUtil;

@Controller
public class PlanController
{
    @Autowired
    PlanService ls;

    @Autowired
    LifeinsService lifeins;

    @RequestMapping("/health")
    @ResponseBody
    @CrossOrigin
    public String health()
    {
        return "success";
    }

    @RequestMapping({"/plan/create.json"})
    @ResponseBody
    public JSONObject create(@RequestBody JSONObject p)
    {
        Plan plan = LifeinsUtil.toPlan(lifeins, p);
        ls.newPlan(plan);

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", LifeinsUtil.jsonOf(plan));

        return res;
    }

    @RequestMapping({"/plan/customer.json"})
    @ResponseBody
    public JSONObject customer(@RequestBody JSONObject p)
    {
        String planId = (String) p.get("planId");

        if (Common.isEmpty(planId))
            throw new RuntimeException("缺少planId");

        Plan plan = ls.getPlan(planId);

        LifeinsUtil.customerOf((Customer) plan.getApplicant(), p.getJSONObject("applicant"));
        LifeinsUtil.customerOf((Customer) plan.getInsurant(), p.getJSONObject("insurant"));

        plan.clearCache();

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", LifeinsUtil.jsonOf(plan));

        return res;
    }

    @RequestMapping({"/plan/view.json"})
    @ResponseBody
    public JSONObject view(@RequestBody JSONObject p)
    {
        String planId = (String) p.get("planId");

        if (Common.isEmpty(planId))
            throw new RuntimeException("缺少planId");

        Plan plan = ls.getPlan(planId);

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", LifeinsUtil.jsonOf(plan));

        return res;
    }

    @RequestMapping({"/plan/edit.json"})
    @ResponseBody
    public JSONObject edit(@RequestBody JSONObject p)
    {
        String planId = (String) p.get("planId");

        if (Common.isEmpty(planId))
            throw new RuntimeException("缺少planId");

        Plan plan = ls.getPlan(planId);
        JSONObject r = LifeinsUtil.jsonOf(plan);

        List<Insurance> rec = ls.getRecommend(plan);
        if (rec != null && !rec.isEmpty())
        {
            JSONArray list = new JSONArray();
            for (Insurance ins : rec)
            {
                JSONObject prd = new JSONObject();
                prd.put("id", ins.getId());
                prd.put("name", ins.getName());
                list.add(prd);
            }
            r.put("recommend", list);
        }

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", r);

        return res;
    }

    @RequestMapping({"/plan/export_keys.json"})
    @ResponseBody
    public JSONObject exportSave(@RequestBody JSONObject p)
    {
        String planId = (String) p.get("planId");

        if (Common.isEmpty(planId))
            throw new RuntimeException("缺少planId");

        Plan plan = ls.getPlan(planId);

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", LifeinsUtil.toSaveJson(plan));

        return res;
    }

    @RequestMapping({"/plan/save.json"})
    @ResponseBody
    public JSONObject save(@RequestBody JSONObject p)
    {
        String planId = (String) p.get("planId");

        if (Common.isEmpty(planId))
            throw new RuntimeException("缺少planId");

        Plan plan = ls.getPlan(planId);
        ls.savePlan(plan);

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", LifeinsUtil.jsonOf(plan));

        return res;
    }

    @RequestMapping({"/plan/remove_clause.json"})
    @ResponseBody
    public JSONObject removeProduct(@RequestBody JSONObject p)
    {
        String planId = (String) p.get("planId");

        if (Common.isEmpty(planId))
            throw new RuntimeException("缺少planId");

        int productIndex = Common.intOf(p.get("index"), -1);

        Plan plan = ls.getPlan(planId);
        plan.remove(productIndex);

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", LifeinsUtil.jsonOf(plan));

        //		System.out.println(res.toJSONString());

        return res;
    }

    @RequestMapping("/plan/list_clause.json")
    @ResponseBody
    public JSONObject listProduct(@RequestBody JSONObject p)
    {
        int parentIndex = Common.intOf(p.get("parentIndex"), -1);

        JSONArray products = new JSONArray();

        if (parentIndex < 0)
        {
            String company = Common.trimStringOf(p.get("company"));
            if (Common.isEmpty(company))
                throw new RuntimeException("缺少company");

            Company insc = lifeins.getCompany(company);

            List<Insurance> list = insc.getProductList();
            for (Insurance ins : list)
            {
                if (ins.isMain())
                {
                    JSONObject m = new JSONObject();
                    m.put("vendor", ins.getVendor());
                    m.put("code", ins.getId());
                    m.put("text", ins.getName());
                    m.put("type", ins.getType() == Insurance.PACKAGE ? "package" : "clause");
                    products.add(m);
                }
            }
        }
        else
        {
            String planId = (String) p.get("planId");
            if (Common.isEmpty(planId))
                throw new RuntimeException("缺少planId");

            Plan plan = ls.getPlan(planId);
            if (plan.isEmpty())
                throw new RuntimeException("plan为空");

            Insurance insurance = plan.getCommodity(parentIndex).getProduct();
            List<String> riderList = insurance.getRiderList();
            if (!Common.isEmpty(riderList))
            {
                for (String rs : riderList)
                {
                    Insurance ins = (Insurance) lifeins.getProduct(rs);
                    if (ins != null)
                    {
                        JSONObject m = new JSONObject();
                        m.put("code", ins.getId());
                        m.put("text", ins.getName());
                        m.put("type", ins.getType() == Insurance.PACKAGE ? "package" : "clause");
                        products.add(m);
                    }
                }
            }
        }

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", products);

        //		System.out.println(res.toJSONString());

        return res;
    }

    @RequestMapping("/plan/view_clause.json")
    @ResponseBody
    public JSONObject viewProduct(@RequestBody JSONObject p)
    {
        String planId = (String) p.get("planId");

        if (Common.isEmpty(planId))
            throw new RuntimeException("缺少planId");

        String productId = (String) p.get("productId");
        int productIndex = Common.intOf(p.get("index"), -1);

        Insurance ins;
        Commodity comm = null;

        if (productIndex < 0) //新建
        {
            ins = (Insurance) lifeins.getProduct(productId);
        }
        else //修改
        {
            Plan plan = ls.getPlan(planId);
            comm = plan.getCommodity(productIndex);
            ins = comm.getProduct();
        }

        JSONObject r = new JSONObject();

        JSONArray items = new JSONArray();

        if (ins.getInput() != null  && !"default".equals(p.get("mode")))
        {
            List<Field> fields = ins.getInput();
            for (Field field : fields)
            {
                JSONObject item = new JSONObject();
                item.put("name", field.getName());
                item.put("label", field.getLabel());
//                item.put("type", field.getType());
                item.put("widget", field.getWidget());
                item.put("detail", field.getOptions().run(null));

                if (comm != null)
                {
                    Object val = comm.getFactor(field.getName());
                    if (val instanceof Boolean)
                        val = ((Boolean)val) ? "Y" : "N";
                    else if (val instanceof Number)
                        val = ((Number)val).intValue();
                    item.put("value", val);
                }

                items.add(item);
            }
        }
        else
        {
            List<String> types = ins.getOptionType();
            for (String type : types)
            {
                JSONObject item = new JSONObject();

                String[] s = getParam(type);
                if (s == null)
                    continue;

                item.put("name", s[0]);
                item.put("label", s[1]);
//                item.put("type", "string");
                item.put("widget", "select");
                item.put("req", true);

                List<String[]> optList = new ArrayList<>();
                for (Option opt : (List<Option>) ins.getOptionList(type))
                    optList.add(new String[] {opt.getCode(), opt.getShow()});
                item.put("detail", optList);

                if (comm != null)
                    item.put("value", comm.getInput(type).getCode());

                items.add(item);
            }

            int inputMode = ins.getInputMode();
            if (inputMode == Purchase.AMOUNT) //需要设定保额
            {
                JSONObject item = new JSONObject();
                item.put("name", "AMOUNT");
                item.put("label", "保额");
//                item.put("type", "number");
                item.put("widget", "number");
                item.put("req", true);
                item.put("value", BigDecimal.valueOf(comm == null ? 200000 : comm.getAmount()).toString());
                items.add(item);
            }
            else if (inputMode == Purchase.QUANTITY) //需要设定份数
            {
                JSONObject item = new JSONObject();
                item.put("name", "QUANTITY");
                item.put("label", "份数");
//                item.put("type", "number");
                item.put("widget", "number");
                item.put("req", true);
                item.put("value", BigDecimal.valueOf(comm == null ? 10 : comm.getQuantity()).toString());
                items.add(item);
            }
            else if (inputMode == Purchase.PREMIUM) //需要设定保费
            {
                JSONObject item = new JSONObject();
                item.put("name", "PREMIUM");
                item.put("label", "保费");
//                item.put("type", "number");
                item.put("widget", "number");
                item.put("req", true);
                item.put("value", BigDecimal.valueOf(comm == null ? 1000 : comm.getPremium()).toString());
                items.add(item);
            }
            else if (inputMode == Purchase.PREMIUM_AND_AMOUNT) //需要设定保额和保费
            {
                JSONObject item = new JSONObject();
                item.put("name", "PREMIUM");
                item.put("label", "保费");
//                item.put("type", "number");
                item.put("widget", "number");
                item.put("req", true);
                item.put("value", BigDecimal.valueOf(comm == null ? 1000 : comm.getPremium()).toString());
                items.add(item);

                item = new JSONObject();
                item.put("name", "AMOUNT");
                item.put("label", "保额");
//                item.put("type", "number");
                item.put("widget", "number");
                item.put("req", true);
                item.put("value", BigDecimal.valueOf(comm == null ? 200000 : comm.getAmount()).toString());
                items.add(item);
            }
        }

        r.put("name", ins.getAbbrName());
        r.put("factors", items);

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", r);

        return res;
    }

    private String[] getParam(String code)
    {
        if ("pay".equals(code))
            return new String[] {"PAY", "交费期间"};
        if ("insure".equals(code))
            return new String[] {"INSURE", "保障期间"};
        return null;
    }

    @RequestMapping("/plan/clause.json")
    @ResponseBody
    public JSONObject setProduct(@RequestBody JSONObject p)
    {
        String productId = (String) p.get("productId");
        int parentIndex = Common.intOf(p.get("parentIndex"), -1);
        int productIndex = Common.intOf(p.get("index"), -1);
        String planId = (String) p.get("planId");

        if (Common.isEmpty(planId))
            throw new RuntimeException("缺少planId");

        Plan plan = ls.getPlan(planId);
        synchronized (plan)
        {
            Commodity c;
            if (productIndex < 0)
            {
                Insurance product = (Insurance) lifeins.getProduct(productId);

                if (parentIndex < 0)
                {
                    c = plan.newCommodity(product);
                }
                else
                {
                    Commodity parent = plan.getCommodity(parentIndex);
                    c = plan.newCommodity(parent, product);
                }
            }
            else
            {
                c = plan.getCommodity(productIndex);
            }

            p = p.getJSONObject("detail");
            if (p != null)
            {
                //自定义模式
                if (c.getProduct().getInput() != null)
                {
                    for (Field f : (List<Field>) c.getProduct().getInput())
                    {
                        Object val = p.get(f.getName());
                        if (val == null)
                            continue;
                        else if ("integer".equalsIgnoreCase(f.getType()))
                            val = Common.intOf(val, 0);
                        else if ("boolean".equalsIgnoreCase(f.getType()))
                            val = Common.boolOf(val, false);

                        c.setValue(f.getName(), val);
                    }
                }
                else
                {
                    String str = "PAY,INSURE,SIM";
                    String num = "PREMIUM,AMOUNT,QUANTITY";

                    for (String s : str.split(","))
                    {
                        Object val = p.get(s);
                        if (val != null)
                            c.setValue(s, val);
                    }

                    for (String s : num.split(","))
                    {
                        BigDecimal val = p.getBigDecimal(s);
                        if (val != null)
                            c.setValue(s, val);
                    }

                    System.out.println(c.getValue());
                }
            }
        }

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", LifeinsUtil.jsonOf(plan));

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
                JSONArray tables = LifeinsUtil.formatTable(c, fold);
                show.put(c.getProduct().getName(), tables);
            }
            else if (c.getChildren() != null)
            {
                for (Commodity child : (List<Commodity>)c.getChildren().toList())
                {
                    if (child.hasFormat("benefit_table"))
                        show.put(child.getProduct().getName(), LifeinsUtil.formatTable(child, fold));
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
                show.put(c.getProduct().getName(), LifeinsUtil.formatChart(c));
            }
            else if (c.getChildren() != null)
            {
                for (Commodity child : (List<Commodity>)c.getChildren().toList())
                {
                    if (child.hasFormat("benefit_chart"))
                        show.put(child.getProduct().getName(), LifeinsUtil.formatChart(child));
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
                coverage.put(c.getProduct().getName(), LifeinsUtil.formatLiability(c));
            }
            else if (c.hasFormat("coverage"))
            {
                coverage.put(c.getProduct().getName(), LifeinsUtil.formatCoverage(c));
            }
            else if (c.getChildren() != null)
            {
                for (Commodity child : (List<Commodity>)c.getChildren().toList())
                {
                    if (child.hasFormat("liability"))
                        coverage.put(child.getProduct().getName(), LifeinsUtil.formatLiability(child));
                    else if (child.hasFormat("coverage"))
                        coverage.put(child.getProduct().getName(), LifeinsUtil.formatCoverage(child));
                }
            }
            res.put("content", coverage);
        }

        return res;
    }

    @RequestMapping({"/plan/fee.json"})
    @ResponseBody
    public JSONObject fee(@RequestBody JSONObject p)
    {
        String planId = (String) p.get("planId");

        if (Common.isEmpty(planId))
            throw new RuntimeException("缺少planId");

        Plan plan = ls.getPlan(planId);

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", LifeinsUtil.feeOf(plan));

        return res;
    }
}
