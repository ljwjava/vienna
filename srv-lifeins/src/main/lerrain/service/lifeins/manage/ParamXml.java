package lerrain.service.lifeins.manage;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lerrain.project.insurance.product.Company;
import lerrain.project.insurance.product.Option;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParamXml implements InsPart
{
    Xml xml;

    public static String[][] PARAM = {{"pay", "缴费方式"}, {"insure", "保障方式"}};
    public static Map PARAM_MAP = new HashMap();

    public ParamXml(Xml xml, Company c)
    {
        this.xml = xml;

        for (String[] param : PARAM)
        {
            Map<String, Option> opts = c.getOptionMap(param[0]);
            JSONArray ja = new JSONArray();
            for (Map.Entry<String, Option> e : opts.entrySet())
                ja.add(new String[] {e.getKey(), e.getValue().getShow()});

            PARAM_MAP.put(param[0], ja);
        }
    }

    @Override
    public JSONObject toJson()
    {
        JSONObject r = new JSONObject();
        r.put("name", "可选择项");
        r.put("code", getCode());

        List<FormField> form = new ArrayList();
        for (String[] param : PARAM)
        {
            Xml pay = xml.firstChild(param[0]);
            JSONObject payVal = new JSONObject();
            for (Xml item : pay.children)
                payVal.put(item.getAttribute("code"), true);
            form.add(FormField.fieldOf(param[0], param[1], "multiswt", PARAM_MAP.get(param[0]), payVal, 3));
        }

        r.put("form", form);
        return r;
    }

    @Override
    public Xml save(JSONObject form)
    {
        for (String label : form.keySet())
        {
            Xml x = new Xml(label);

            JSONArray array = form.getJSONArray("label");
            for (int i=0;i<array.size();i++)
            {
                Xml c = new Xml("item");
                c.setAttribute("code", array.getString(i));
                x.addChild(c);
            }

            xml.replace(x);
        }

        return xml;
    }

    @Override
    public String getCode()
    {
        return "param";
    }
}
