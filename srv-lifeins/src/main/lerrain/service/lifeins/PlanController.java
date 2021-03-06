package lerrain.service.lifeins;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import lerrain.project.insurance.plan.Commodity;
import lerrain.project.insurance.plan.Plan;
import lerrain.project.insurance.product.*;

import lerrain.project.insurance.product.rule.Rule;
import lerrain.project.insurance.product.rule.RuleUtil;
import lerrain.service.common.Log;
import lerrain.service.common.ServiceTools;
import lerrain.service.lifeins.manage.EditorService;
import lerrain.service.lifeins.quest.MergeQuestService;
import lerrain.tool.Common;
import lerrain.tool.script.Script;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import javax.annotation.PostConstruct;

@Controller
public class PlanController
{
    @Autowired
    PlanService planSrv;

    @Autowired
    LifeinsService lifeins;

    @Autowired
    MergeQuestService mergeQuestSrv;

    @Autowired
    EditorService editorSrv;

    @Autowired
    MsgQueue queue;

    @Autowired
    ScriptService scriptSrv;

    @Autowired
    ServiceTools tools;

    @Value("${env}")
    String srvEnv;

    List numFactors = JSON.parseArray("['PREMIUM','AMOUNT','QUANTITY']");

    @PostConstruct
    @RequestMapping({"/reset"})
    @ResponseBody
    public String reset()
    {
        //Script.STACK_MESSAGE = !("prd".equalsIgnoreCase(srvEnv));
        //Log.info("ENV: " + srvEnv + ", log of formula stack: " + Script.STACK_MESSAGE);

        //Log.EXCEPTION_STACK = Script.STACK_MESSAGE;

        lifeins.reset();
        planSrv.reset();
        scriptSrv.reset();

        mergeQuestSrv.reset();

        //editorSrv.reset();

        queue.start();

        return "success";
    }

    @RequestMapping("/plan/create.json")
    @ResponseBody
    public JSONObject create(@RequestBody JSONObject p)
    {
        String planId = p.getString("planId");
        if (Common.isEmpty(planId))
            planId = tools.nextId("plan") + "";

        Plan plan = LifeinsUtil.toPlan(lifeins, p, planId);
        planSrv.newPlan(plan);

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", LifeinsUtil.jsonOf(plan));

        return res;
    }

    @RequestMapping("/plan/customer.json")
    @ResponseBody
    public JSONObject customer(@RequestBody JSONObject p)
    {
        String planId = p.getString("planId");

        if (Common.isEmpty(planId))
            throw new RuntimeException("缺少planId");

        Plan plan = planSrv.getPlan(planId);

        LifeinsUtil.customerOf((Customer) plan.getApplicant(), p.getJSONObject("applicant"));
        LifeinsUtil.customerOf((Customer) plan.getInsurant(), p.getJSONObject("insurant"));

        plan.clearCache();

        queue.add(plan);

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", LifeinsUtil.jsonOf(plan));

        return res;
    }

    @RequestMapping("/plan/view.json")
    @ResponseBody
    public JSONObject view(@RequestBody JSONObject p)
    {
        String planId = p.getString("planId");

        if (Common.isEmpty(planId))
            throw new RuntimeException("缺少planId");

        Plan plan = planSrv.getPlan(planId);

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", LifeinsUtil.jsonOf(plan));

        return res;
    }

    @RequestMapping("/plan/edit.json")
    @ResponseBody
    public JSONObject edit(@RequestBody JSONObject p)
    {
        String planId = p.getString("planId");

        if (Common.isEmpty(planId))
            throw new RuntimeException("缺少planId");

        Plan plan = planSrv.getPlan(planId);
        JSONObject r = LifeinsUtil.jsonOf(plan);

        List<Insurance> rec = planSrv.getRecommend(plan);
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
        String planId = p.getString("planId");

        if (Common.isEmpty(planId))
            throw new RuntimeException("缺少planId");

        Plan plan = planSrv.getPlan(planId);

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", LifeinsUtil.toSaveJson(plan));

        return res;
    }

//    @RequestMapping({"/plan/save.json"})
//    @ResponseBody
//    public JSONObject save(@RequestBody JSONObject p)
//    {
//        String planId = (String) p.get("planId");
//
//        if (Common.isEmpty(planId))
//            throw new RuntimeException("缺少planId");
//
//        Plan plan = planSrv.getPlan(planId);
//        planSrv.savePlan(plan);
//
//        JSONObject res = new JSONObject();
//        res.put("result", "success");
//        res.put("content", LifeinsUtil.jsonOf(plan));
//
//        return res;
//    }

    @RequestMapping({"/plan/remove_clause.json"})
    @ResponseBody
    public JSONObject removeProduct(@RequestBody JSONObject p)
    {
        String planId = p.getString("planId");

        if (Common.isEmpty(planId))
            throw new RuntimeException("缺少planId");

        int productIndex = Common.intOf(p.get("index"), -1);
        String productId = p.getString("productId");

        Plan plan = planSrv.getPlan(planId);
        if (productId == null) //没传productId直接移除index位置的产品
        {
            plan.remove(productIndex);
        }
        else //传了productId，则移除index的附加险里面该产品
        {
            Commodity parent = plan.getCommodity(productIndex);
            for (int i = plan.size() - 1; i >= 0; i--)
            {
                Commodity c = plan.getCommodity(i);
                if (c.getParent() == parent && productId.equals(c.getProduct().getId()))
                    plan.remove(i);
            }
        }

        queue.add(plan);

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", LifeinsUtil.jsonOf(plan));

        return res;
    }

    private JSONObject clauseOf(Insurance ins, Plan plan)
    {
        JSONObject m = new JSONObject();
        m.put("id", ins.getId());
        m.put("code", ins.getId());
        m.put("vendor", ins.getVendor());
        m.put("company", ins.getCompany().getId());
        m.put("name", ins.getName());
        m.put("classify", ins.getAdditional("classify"));
        m.put("tag", ins.getAdditional("tag"));
        m.put("remark", ins.getAdditional("remark"));
        m.put("type", !ins.isMain() ? "rider" : ins.getType() == Insurance.PACKAGE ? "package" : "clause");

        if (plan != null)
        {
            List<Rule> list = ins.getRuleList() == null ? null : RuleUtil.precheck(ins, plan);
            if (list != null && !list.isEmpty())
            {
                JSONArray text = new JSONArray();
                for (Rule rule : list)
                    text.add(rule.getDesc());
                m.put("rule", text);
            }
        }

        return m;
    }

    @RequestMapping("/plan/find_clause.json")
    @ResponseBody
    public JSONObject listProduct(@RequestBody JSONArray p)
    {
        JSONArray r = new JSONArray();
        for (int i=0;i<p.size();i++)
        {
            Insurance ins = lifeins.getProduct(p.getString(i));
            if (ins == null || !ins.isMain())
                continue;

            r.add(clauseOf(ins, null));
        }

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", r);

        return res;
    }

    @RequestMapping("/plan/list_riders.json")
    @ResponseBody
    public JSONObject listRiders(@RequestBody JSONObject p)
    {
        String planId = p.getString("planId");
        if (Common.isEmpty(planId))
            throw new RuntimeException("缺少planId");

        Plan plan = planSrv.getPlan(planId);
        if (plan.isEmpty())
            throw new RuntimeException("plan为空");

        int index = Common.intOf(p.get("index"), -1);

        Insurance insurance = plan.getCommodity(index).getProduct();
        List<String> riderList = insurance.getRiderList();

        JSONArray rm = new JSONArray();
        if (!Common.isEmpty(riderList)) for (String rs : riderList)
        {
            Insurance ins = lifeins.getProduct(rs);
            if (ins != null)
                rm.add(clauseOf(ins, plan));
        }

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", rm);

        return res;
    }

    @RequestMapping("/plan/list_clause.json")
    @ResponseBody
    public JSONObject listProduct(@RequestBody JSONObject p)
    {
        String productId = p.getString("productId");
        int parentIndex = Common.intOf(p.get("parentIndex"), -1);

        String planId = p.getString("planId");
        Plan plan = planId == null ? null : planSrv.getPlan(planId);

        JSONArray prdIds = p.getJSONArray("scope");

        JSONArray products = new JSONArray();

        if (Common.isEmpty(productId) && parentIndex < 0)
        {
            String company = Common.trimStringOf(p.get("company"));
            if (Common.isEmpty(company))
            {
                if (prdIds == null)
                    throw new RuntimeException("缺少company&scope");

                for (int i=0;i<prdIds.size();i++)
                {
                    Insurance ins = lifeins.getProduct(prdIds.getString(i));
                    products.add(clauseOf(ins, plan));
                }
            }
            else
            {
                Company insc = lifeins.getCompany(company);

                List<Insurance> list = insc.getProductList();
                for (Insurance ins : list)
                {
                    if ((ins != null && ins.isMain()) && (prdIds == null || prdIds.indexOf(ins.getId()) >= 0))
                        products.add(clauseOf(ins, plan));
                }
            }
        }
        else
        {
            Insurance insurance;
            if (parentIndex >= 0)
            {
                if (plan == null || plan.isEmpty())
                    throw new RuntimeException("plan为空");

                insurance = plan.getCommodity(parentIndex).getProduct();
            }
            else
            {
                insurance = lifeins.getProduct(productId);
            }

            List<String> riderList = insurance.getRiderList();
            if (!Common.isEmpty(riderList))
            {
                for (String rs : riderList)
                {
                    Insurance ins = lifeins.getProduct(rs);
                    if (ins != null && (prdIds == null || prdIds.indexOf(ins.getId()) >= 0))
                        products.add(clauseOf(ins, plan));
                }
            }
        }

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", products);

        return res;
    }

    @RequestMapping("/plan/view_clause.json")
    @ResponseBody
    public JSONObject viewProduct(@RequestBody JSONObject p)
    {
        String planId = p.getString("planId");

        if (Common.isEmpty(planId))
            throw new RuntimeException("缺少planId");

        String productId = p.getString("productId");
        int productIndex = Common.intOf(p.get("index"), -1);

        Insurance ins;
        Commodity comm = null;

        if (productIndex < 0) //新建
        {
            ins = lifeins.getProduct(productId);
        }
        else //修改
        {
            Plan plan = planSrv.getPlan(planId);
            comm = plan.getCommodity(productIndex);
            ins = comm.getProduct();
        }

        JSONObject r = new JSONObject();

        JSONArray items = new JSONArray();

        List<Field> fields = (List<Field>) ins.getAdditional("input");
        if (p != null && "default".equals(p.get("mode")))
            fields = null;
        else if (fields == null || fields.isEmpty())
            fields = ins.getInput();

        if (fields != null && !fields.isEmpty())
        {
            for (Field field : fields)
            {
                if (field == null)
                    continue;

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
                item.put("widget", "switch");
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
                item.put("value", BigDecimal.valueOf(comm == null ? 200000 : comm.getAmount()).intValue());
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
                item.put("value", BigDecimal.valueOf(comm == null ? 10 : comm.getQuantity()).intValue());
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
                item.put("value", BigDecimal.valueOf(comm == null ? 1000 : comm.getPremium()).intValue());
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
                item.put("value", BigDecimal.valueOf(comm == null ? 1000 : comm.getPremium()).intValue());
                items.add(item);

                item = new JSONObject();
                item.put("name", "AMOUNT");
                item.put("label", "保额");
//                item.put("type", "number");
                item.put("widget", "number");
                item.put("req", true);
                item.put("value", BigDecimal.valueOf(comm == null ? 200000 : comm.getAmount()).intValue());
                items.add(item);
            }
        }

        r.put("name", ins.getName());
        r.put("abbrName", ins.getAbbrName());
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

    @RequestMapping("/plan/fast_clause.json")
    @ResponseBody
    public JSONObject setProductFast(@RequestBody JSONObject p)
    {
        setProductVal(p);

        JSONObject res = new JSONObject();
        res.put("result", "success");

        return res;
    }

    public Plan setProductVal(@RequestBody JSONObject p)
    {
        String productId = p.getString("productId");
        int parentIndex = Common.intOf(p.get("parentIndex"), -1);
        int productIndex = Common.intOf(p.get("index"), -1);
        String planId = p.getString("planId");

        if (Common.isEmpty(planId))
            throw new RuntimeException("缺少planId");

        p = p.getJSONObject("detail");

        Plan plan = planSrv.getPlan(planId);
        synchronized (plan)
        {
            Commodity c;
            if (productIndex < 0)
            {
                Insurance product = lifeins.getProduct(productId);

                if (parentIndex < 0)
                {
                    if (!plan.isEmpty() && plan.primaryCommodity().getProduct().getCurrency() != product.getCurrency())
                        throw new RuntimeException("同一投保计划要求币种相同");

                    c = plan.newCommodity(product);
                }
                else
                {
                    Commodity parent = plan.getCommodity(parentIndex);
                    if (parent.getRider(product.getId()) != null)
                        throw new RuntimeException("同一主险下，不可重复添加附加险，请修改原产品");

                    c = plan.newCommodity(parent, product);
                }
            }
            else
            {
                c = plan.getCommodity(productIndex);
            }

            List<Field> fields = (List<Field>)c.getProduct().getAdditional("input");
            if (p != null && "default".equals(p.get("mode")))
                fields = null;
            else if (fields == null || fields.isEmpty())
                fields = c.getProduct().getInput();

            if (fields != null && !fields.isEmpty())
            {
                if (productIndex < 0 && p == null)
                {
                    p = new JSONObject();
                    for (Field f : fields)
                    {
                        if (f != null && f.getValue() != null)
                            p.put(f.getName(), f.getValue());
                    }
                }

                if (p != null) for (Field f : fields)
                {
                    if (f == null || f.getType() == null)
                        continue;

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
            else if (p != null)
            {
                for (Map.Entry<String, Object> e : p.entrySet())
                {
                    if (e.getValue() != null)
                    {
                        if (numFactors.indexOf(e.getKey()) >= 0)
                            c.setValue(e.getKey(), Common.decimalOf(e.getValue()));
                        else
                            c.setValue(e.getKey(), e.getValue());
                    }
                }
            }

            queue.add(plan);
        }

        return plan;
    }

    @RequestMapping("/plan/clause.json")
    @ResponseBody
    public JSONObject setProduct(@RequestBody JSONObject p)
    {
        Plan plan = setProductVal(p);

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", LifeinsUtil.jsonOf(plan));

        return res;
    }

    @RequestMapping({"/plan/fee.json"})
    @ResponseBody
    public JSONObject fee(@RequestBody JSONObject p)
    {
        String planId = p.getString("planId");

        if (Common.isEmpty(planId))
            throw new RuntimeException("缺少planId");

        Plan plan = planSrv.getPlan(planId);

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", LifeinsUtil.feeOf(plan));

        return res;
    }

    @RequestMapping({"/plan/quest/merge.json"})
    @ResponseBody
    public JSONObject mergeQuest(@RequestBody JSONObject p)
    {
        String planId = p.getString("planId");

        if (Common.isEmpty(planId))
            throw new RuntimeException("缺少planId");

        Plan plan = planSrv.getPlan(planId);

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", mergeQuestSrv.refreshQuestText(plan));

        return res;
    }

    @RequestMapping("/plan/rebuild.json")
    @ResponseBody
    public JSONObject rebuild(@RequestBody JSONObject q)
    {
        String planId = q.getString("planId");

        if (Common.isEmpty(planId))
            throw new RuntimeException("缺少planId");

        JSONArray x = q.getJSONArray("detail");

        Plan plan = planSrv.getPlan(planId);
        plan.removeAll();

        synchronized (plan)
        {
            Map<String, Commodity> main = new HashMap();

            for (int i = 0; i < x.size(); i++)
            {
                JSONObject p = x.getJSONObject(i);
                String productId = p.getString("productId");
                String parentId = p.getString("parentId");

                Commodity c;
                if (parentId == null)
                {
                    c = plan.newCommodity(lifeins.getProduct(productId));
                    main.put(productId, c);
                }
                else
                {
                    c = plan.newCommodity(main.get(parentId), lifeins.getProduct(productId));
                }

                List<Field> fields = (List<Field>) c.getProduct().getAdditional("input");
                if (p != null && "default".equals(p.get("mode")))
                    fields = null;
                else if (fields == null || fields.isEmpty())
                    fields = c.getProduct().getInput();

                p = p.getJSONObject("factors");

                if (fields != null && !fields.isEmpty())
                {
                    if (p == null)
                    {
                        p = new JSONObject();
                        for (Field f : fields)
                        {
                            if (f != null && f.getValue() != null)
                                p.put(f.getName(), f.getValue());
                        }
                    }

                    if (p != null) for (Field f : fields)
                    {
                        if (f == null || f.getType() == null)
                            continue;

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
                else if (p != null)
                {
                    for (Map.Entry<String, Object> e : p.entrySet())
                    {
                        if (e.getValue() != null)
                        {
                            if (numFactors.indexOf(e.getKey()) >= 0)
                                c.setValue(e.getKey(), Common.decimalOf(e.getValue()));
                            else
                                c.setValue(e.getKey(), e.getValue());
                        }
                    }
                }
            }
        }

        queue.add(plan);

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", LifeinsUtil.jsonOf(plan));

        return res;
    }
}
