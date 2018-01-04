package lerrain.service.lifeins.manage;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lerrain.tool.Common;

import java.util.ArrayList;
import java.util.List;

public class BaseXml implements InsPart
{
    private static final Object DEF_TYPE = JSON.parseArray("[['life','寿险'],['thunder','重疾'],['medical','医疗']]");
    private static final Object DEF_INPUT = JSON.parseArray("[['default','默认'],['amount','保额'],['premium','保费']]");
    private static final Object DEF_PURCHASE = JSON.parseArray("[['default','默认'],['amount','保额'],['premium','保费']]");

    Xml xml;

    public BaseXml(Xml xml)
    {
        this.xml = xml;
    }

    @Override
    public JSONObject toJson()
    {
        JSONObject base = new JSONObject();
        base.put("code", getCode());
        base.put("name", "基础信息");

        List<FormField> form = new ArrayList();
        form.add(FormField.fieldOf("id", "ID", "text", null, null));
        form.add(FormField.fieldOf("code", "CODE", "text", null, null));
        form.add(FormField.fieldOf("name", "名称", "text", null, null));
        form.add(FormField.fieldOf("name_abbr", "名称缩写", "text", null, null));
        form.add(FormField.fieldOf("unit", "单位", "text", null, null));
        form.add(FormField.fieldOf("type", "产品类型", "select", DEF_TYPE, "life"));
        form.add(FormField.fieldOf("input", "录入方式", "select", DEF_INPUT, "default"));
        form.add(FormField.fieldOf("purchase", "计量方式", "select", DEF_PURCHASE, "default"));
        form.add(FormField.fieldOf("sequence", "排序值", "text", null, null));
        form.add(FormField.fieldOf("sale_begin_date", "起售时间", "date", null, null));
        form.add(FormField.fieldOf("sale_end_date", "停售时间", "date", null, null));
        base.put("form", form);

        return base;
    }

    @Override
    public Xml save(JSONObject form)
    {
        xml.setAttribute("id", form.getString("id"));
        xml.setAttribute("code", form.getString("code"));
        xml.setAttribute("name", form.getString("name"));
        xml.setAttribute("name_abbr", form.getString("name_abbr"));
        xml.setAttribute("unit", form.getString("unit"));
        xml.setAttribute("type", form.getString("type"));
        xml.setAttribute("sequence", form.getString("sequence"));

        String input = form.getString("input");
        xml.setAttribute("input", "default".equals(input) ? null : input);
        String purchase = form.getString("purchase");
        xml.setAttribute("purchase", "default".equals(purchase) ? null : purchase);
        String saleBeginDate = form.getString("sale_begin_date");
        xml.setAttribute("sale_begin_date", Common.isEmpty(saleBeginDate) ? null : saleBeginDate);
        String saleEndDate = form.getString("sale_end_date");
        xml.setAttribute("sale_end_date", Common.isEmpty(saleEndDate) ? null : saleEndDate);

        return xml;
    }

    @Override
    public String getCode()
    {
        return "base";
    }
}
