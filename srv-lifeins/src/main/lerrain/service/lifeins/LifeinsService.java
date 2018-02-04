package lerrain.service.lifeins;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lerrain.project.insurance.product.Company;
import lerrain.project.insurance.product.Config;
import lerrain.project.insurance.product.Insurance;
import lerrain.project.insurance.product.InsuranceMgr;

import lerrain.project.insurance.product.rule.Rule;
import lerrain.service.common.Log;
import lerrain.service.lifeins.format.BenefitFilter;
import lerrain.service.lifeins.format.BenefitParser;
import lerrain.service.lifeins.format.FGraphParser;
import lerrain.tool.Common;
import lerrain.tool.formula.FormulaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service("lifeins")
public class LifeinsService
{
    @Autowired
    LifeinsDao lifeinsDao;

    private Map<String, Company> company;

    private Map<String, Insurance> product;

    @Value("${path.lifeins}")
    String dataPath;

    public void reset()
    {
//        Config.addFilter("fgraph", new FGraphFilter());
        Config.addFilter("benefit", new BenefitFilter());

        Config.addParser("fgraph", new FGraphParser());
        Config.addParser("benefit", new BenefitParser());

        try
        {
            product = new HashMap<>();

            System.out.println("CLAUSE PATH: " + dataPath);

            InsuranceMgr ins = InsuranceMgr.managerOf(dataPath, "insurance.xml");
            company = ins.load();

            lifeinsDao.loadPacks();

            for (Entry<String, Company> e : company.entrySet())
            {
                if (e.getValue().getProductList() != null)
                {
                    for (Insurance prd : (List<Insurance>) e.getValue().getProductList())
                        addProduct(prd);
                }
            }

            lifeinsDao.supplyClauses();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

//    public void addRule(Map r)
//    {
//        for (String productId : r.keySet())
//        {
//            Insurance ins = this.getProduct(productId);
//            if (ins == null)
//                continue;
//
//            JSONArray rs = r.getJSONArray(productId);
//            for (int i = 0; i < rs.size(); i++)
//            {
//                JSONObject rp = rs.getJSONObject(i);
//
//                String c = rp.getString("condition");
//                JSONArray rules = rp.getJSONArray("rules");
//
//                for (int j = 0; j < rules.size(); j++)
//                {
//                    Rule rule = new Rule();
//
//                    JSONObject rj = rules.getJSONObject(j);
//
//                    String id = rj.getString("id");
//                    if (!Common.isEmpty(id))
//                        rule.setId(id);
//
//                    String condition = rj.getString("condition");
//                    if (!Common.isEmpty(condition))
//                    {
//                        String str = c == null ? condition : "(" + c + ") and (" + condition + ")";
//                        rule.setCondition(FormulaUtil.formulaOf(str));
//
//                        String desc = rj.getString("desc");
//                        if (desc != null)
//                            rule.setDesc(FormulaUtil.formulaOf(desc));
//                        else
//                            rule.setDesc(rj.getString("text"));
//                    }
//                    else if (c == null) //自由型规则，不支持分类
//                    {
//                        rule.setCondition(FormulaUtil.formulaOf(rj.getString("script")));
//                    }
//
//                    String level = rj.getString("level");
//                    if ("alert".equalsIgnoreCase(level))
//                        rule.setLevel(Rule.LEVEL_ALERT);
//
//                    String type = rj.getString("type");
//                    if ("customer".equalsIgnoreCase(type))
//                        rule.setType(Rule.TYPE_CUSTOMER);
//                    else if ("product".equalsIgnoreCase(type))
//                        rule.setType(Rule.TYPE_PRODUCT);
//                    else if ("plan".equalsIgnoreCase(type))
//                        rule.setType(Rule.TYPE_PLAN);
//                    else if ("proposal".equalsIgnoreCase(type))
//                        rule.setType(Rule.TYPE_PROPOSAL);
//
//                    String alert = rj.getString("alert");
//                    if (!Common.isEmpty(alert))
//                    {
//                        for (String alertCol : alert.split(","))
//                            rule.addAlert(alertCol);
//                    }
//
//                    ins.addRule(rule);
//                }
//            }
//        }
//    }

    public void addProduct(Insurance ins)
    {
        Log.debug("load <%s>%s", ins.getId(), ins.getName());
        product.put(ins.getId(), ins);
    }

    public Map<String, Company> getAllCompany()
    {
        return company;
    }

    public Company getCompany(String vendor)
    {
        if (vendor == null)
            return company.get("nci");

        return company.get(vendor);
    }

    public Insurance getProduct(String productId)
    {
        return product.get(productId);
    }
}
