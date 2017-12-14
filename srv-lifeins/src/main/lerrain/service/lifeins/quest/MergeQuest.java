package lerrain.service.lifeins.quest;

import lerrain.tool.formula.Formula;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MergeQuest
{
    String code;
    String text;

    Formula condition;

    Map<String, Formula> vars = new HashMap<>();

    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

    public Map<String, Formula> getVars()
    {
        return vars;
    }

    public void setVars(Map<String, Formula> vars)
    {
        this.vars = vars;
    }

    public String getText()
    {
        return text;
    }

    public void setText(String text)
    {
        this.text = text;

//        int p1, p2 = -1;
//        while ((p1 = text.indexOf("{", p2 + 1)) >= 0)
//        {
//            p2 = text.indexOf("}", p1 + 1);
//
//            String var = text.substring(p1 + 1, p2 - p1 - 1);
//            vars.add(var);
//        }
    }

    public Formula getCondition()
    {
        return condition;
    }

    public void setCondition(Formula condition)
    {
        this.condition = condition;
    }
}
