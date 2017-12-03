package lerrain.service.lifeins.plan.quest;

import lerrain.project.insurance.plan.Commodity;
import lerrain.project.insurance.plan.Plan;
import lerrain.project.insurance.product.Insurance;
import lerrain.tool.Common;
import lerrain.tool.formula.Formula;
import lerrain.tool.formula.Value;
import lerrain.tool.script.Stack;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MergeQuestService
{
    @Autowired
    MergeQuestDao questDao;

    Stack env;

    List<MergeQuest> list;

    public void reset()
    {
        questDao.loadClauseFactors();

        list = questDao.loadAllQuests();

        env = new Stack(questDao.loadQuestConst());
    }

    public List<String> refreshQuestText(Plan plan)
    {
        Map<MergeQuest, Map<String, List>> temp = new LinkedHashMap<>();

        for (MergeQuest quest : list)
        {
            if (quest.getCondition() != null && !Value.booleanOf(quest.getCondition(), plan.getFactors()))
                continue;

            for (int i = 0; i < plan.size(); i++)
            {
                Commodity c = plan.getCommodity(i);
                Insurance ins = c.getProduct();

                if (ins.getAdditional(quest.getCode()) != null)
                {
                    Map<String, List> vals;
                    if (!temp.containsKey(quest))
                    {
                        vals = new HashMap<>();
                        for (String var : quest.getVars().keySet())
                            vals.put(var, new ArrayList());

                        temp.put(quest, vals);
                    }
                    else
                    {
                        vals = temp.get(quest);
                    }

                    for (String var : quest.getVars().keySet())
                    {
                        List list = vals.get(var);
                        list.add(c.getAdditional(quest.getCode() + "_" + var));
                    }
                }
            }
        }

        List<String> r = new ArrayList<>();
        for (Map.Entry<MergeQuest, Map<String, List>> q : temp.entrySet())
        {
            MergeQuest quest = q.getKey();
            String text = quest.getText();

            Map<String, List> map = q.getValue();
            for (Map.Entry<String, List> v : map.entrySet())
            {
                Formula f = quest.getVars().get(v.getKey());
                if (f != null)
                {
                    env.declare("self", v.getValue());
                    text = text.replace("{" + v.getKey() + "}", Value.stringOf(quest.getVars().get(v.getKey()), env));
                }
            }

            r.add(text);
        }

        return r;
    }
}
