package lerrain.service.sale;

import lerrain.tool.formula.Formula;

/**
 * Created by lerrain on 2017/5/6.
 */
public class InputField
{
    String name;
    String var;
    String type;
    String widget;

    Object label;
    String[] scope;

    Formula condition;
    Formula detail;

    Object value;

    public Formula getDetail()
    {
        return detail;
    }

    public void setDetail(Formula detail)
    {
        this.detail = detail;
    }

    public Object getLabel()
    {
        return label;
    }

    public void setLabel(Object label)
    {
        this.label = label;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getVar()
    {
        return var;
    }

    public void setVar(String var)
    {
        this.var = var;
    }

    public String[] getScope()
    {
        return scope;
    }

    public void setScope(String[] scope)
    {
        this.scope = scope;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public Object getValue()
    {
        return value;
    }

    public void setValue(Object value)
    {
        this.value = value;
    }

    public Formula getCondition()
    {
        return condition;
    }

    public void setCondition(Formula condition)
    {
        this.condition = condition;
    }

    public String getWidget()
    {
        return widget;
    }

    public void setWidget(String widget)
    {
        this.widget = widget;
    }
}
