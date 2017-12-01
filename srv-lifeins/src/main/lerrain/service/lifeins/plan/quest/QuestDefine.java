package lerrain.service.lifeins.plan.quest;

import lerrain.tool.formula.Formula;

public class QuestDefine
{
    String code;
    String text;

    Formula condition;

    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

    public String getText()
    {
        return text;
    }

    public void setText(String text)
    {
        this.text = text;
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
