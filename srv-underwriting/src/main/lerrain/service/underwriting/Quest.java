package lerrain.service.underwriting;

import lerrain.tool.formula.Formula;

public class Quest
{
    public static final int WIDGET_SWITCH   = 1;
    public static final int WIDGET_SELECT   = 2;
    public static final int WIDGET_INPUT    = 3;

    String code;

    int type;
    Formula condition;
    String disease;

    String text;
    Object answer;
    String next;

    int widget;
    String feature;

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

    public int getType()
    {
        return type;
    }

    public void setType(int type)
    {
        this.type = type;
    }

    public String getDisease()
    {
        return disease;
    }

    public void setDisease(String disease)
    {
        this.disease = disease;
    }

    public String getText()
    {
        return text;
    }

    public void setText(String text)
    {
        this.text = text;
    }

    public Object getAnswer()
    {
        return answer;
    }

    public void setAnswer(Object answer)
    {
        this.answer = answer;
    }

    public String getNext()
    {
        return next;
    }

    public void setNext(String next)
    {
        this.next = next;
    }

    public int getWidget()
    {
        return widget;
    }

    public void setWidget(int widget)
    {
        this.widget = widget;
    }

    public String getFeature()
    {
        return feature;
    }

    public void setFeature(String feature)
    {
        this.feature = feature;
    }
}
