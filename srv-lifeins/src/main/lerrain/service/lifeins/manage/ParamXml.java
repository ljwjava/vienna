package lerrain.service.lifeins.manage;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ParamXml implements InsPart
{
    Xml xml;

    public ParamXml(Xml xml)
    {
        this.xml = xml;
    }

    @Override
    public JSONObject toJson()
    {
        JSONObject r = new JSONObject();
        r.put("name", "可选择项");
        r.put("code", getCode());

        List<FormField> form = new ArrayList();
        form.add(FormField.fieldOf("pay", "缴费方式", "mutiswt", null, null));
        form.add(FormField.fieldOf("insure", "保障方式", "mutiswt", null, null));
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
