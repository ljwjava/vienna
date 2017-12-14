package lerrain.service.lifeins.quest;

import lerrain.project.insurance.plan.Plan;
import lerrain.tool.formula.Formula;
import org.springframework.stereotype.Service;

import java.util.List;

public class TradQuest
{
    Long id;
    String code;

    int type;

    Object detail;
    Formula condition;

    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

    public Formula getCondition()
    {
        return condition;
    }

    public void setCondition(Formula condition)
    {
        this.condition = condition;
    }

    public Object getDetail()
    {
        return detail;
    }

    public void setDetail(Object detail)
    {
        this.detail = detail;
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public int getType()
    {
        return type;
    }

    public void setType(int type)
    {
        this.type = type;
    }
}
