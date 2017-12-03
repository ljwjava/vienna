package lerrain.service.lifeins.plan.quest;

import lerrain.project.insurance.plan.Input;
import lerrain.project.insurance.plan.Plan;
import lerrain.tool.formula.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TradQuestService
{
    @Autowired
    TradQuestDao questDao;

    Map<String, List<TradQuest>> cache = new HashMap<>();

    public Map<String, String> getQuestText(Object quests)
    {
        Map r = new HashMap();

        if (quests instanceof List) for (Object v : (List)quests)
        {
            if (v instanceof Map)
            {
                Map m = (Map) v;
                if (m.containsKey("code") && m.containsKey("quest"))
                {
                    r.put(m.get("code"), m.get("quest"));
                }
                else if (m.containsKey("option"))
                {
                    Map r1 = getQuestText(m.get("option"));
                    if (r1 != null) r.putAll(r1);
                }
            }
        }

        return r;
    }

    public List<Object> getQuests(Plan plan, int customerType)
    {
        List<Object> r = new ArrayList<>();

        String company = plan.primaryCommodity().getProduct().getVendor();

        boolean exempt = false;
        for (int i = 0; i < plan.size(); i++)
        {
            Input pay = plan.getCommodity(i).getPay();
            if (pay != null && "exempt".equals(pay.getCode()))
            {
                exempt = true;
                break;
            }
        }

        boolean forSelf = plan.getApplicant().equals(plan.getInsurant());
        if (forSelf && customerType == 2)
            return null;

        List<TradQuest> list = getQuests(company, 1, customerType);
        if (list != null) for (TradQuest q : list)
        {
            if (!forSelf && !exempt && customerType == 1 && q.getType() == 2) //如果没有豁免，并且投保人不是被保人，投保人不需要填健康告知
                continue;

            if (q.getCondition() == null)
                r.add(q.getDetail());
            else if (Value.booleanOf(q.getCondition(), customerType == 1 ? plan.getApplicant() : customerType == 2 ? plan.getInsurant() : null))
                r.add(q.getDetail());
        }

        return r;
    }

    private List<TradQuest> getQuests(String company, int applyType, int customerType)
    {
        synchronized (cache)
        {
            List<TradQuest> list = cache.get(company + "/" + applyType + "/" + customerType);
            if (list == null)
            {
                list = questDao.getQuests(company, applyType, customerType);
                cache.put(company + "/" + applyType + "/" + customerType, list);
            }
            return list;
        }
    }
}
