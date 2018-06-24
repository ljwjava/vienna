package lerrain.service.lifeins.format;

import lerrain.tool.formula.Formula;

public class LiabGraphItem
{
    String type;
    String mode;

    Formula value;
    Formula text;

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getMode()
    {
        return mode;
    }

    public void setMode(String mode)
    {
        this.mode = mode;
    }

    public Formula getValue()
    {
        return value;
    }

    public void setValue(Formula value)
    {
        this.value = value;
    }

    public Formula getText()
    {
        return text;
    }

    public void setText(Formula text)
    {
        this.text = text;
    }
}
