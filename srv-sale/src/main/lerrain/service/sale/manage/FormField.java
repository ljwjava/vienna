package lerrain.service.sale.manage;

/**
 * Created by lerrain on 2017/5/6.
 */
public class FormField
{
    String code;
    String label;
    String widget;

    Object detail;
    Object value;

    int length;

    public Object getDetail()
    {
        return detail;
    }

    public void setDetail(Object detail)
    {
        this.detail = detail;
    }

    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

    public String getLabel()
    {
        return label;
    }

    public void setLabel(String label)
    {
        this.label = label;
    }

    public int getLength()
    {
        return length;
    }

    public void setLength(int length)
    {
        this.length = length;
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

    public static FormField fieldOf(String code, String label, String widget, Object detail, Object value)
    {
        return fieldOf(code, label, widget, detail, value, 1);
    }

    public static FormField fieldOf(String code, String label, String widget, Object detail, Object value, int length)
    {
        FormField field = new FormField();
        field.code = code;
        field.label = label;
        field.widget = widget;
        field.detail = detail;
        field.value = value;
        field.length = length;

        return field;
    }
}
