package lerrain.service.lifeins.pack;

/**
 * Created by lerrain on 2017/5/6.
 */
public class InputField
{
    String name;
    String var;
    String label;
    String type;
    String widget;

    String[] scope;

    Object detail;
    Object value;

    public Object getDetail()
    {
        return detail;
    }

    public void setDetail(Object detail)
    {
        this.detail = detail;
    }

    public String getLabel()
    {
        return label;
    }

    public void setLabel(String label)
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

    public String getWidget()
    {
        return widget;
    }

    public void setWidget(String widget)
    {
        this.widget = widget;
    }
}
