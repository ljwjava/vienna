package lerrain.service.lifeins.manage;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lerrain.project.insurance.product.Company;
import lerrain.project.insurance.product.Insurance;
import lerrain.tool.Common;

import java.util.ArrayList;
import java.util.List;

public class InitXml implements InsPart
{
    Xml xml;

    public InitXml(Xml xml)
    {
        this.xml = xml;
    }

    @Override
    public JSONObject toJson()
    {
        JSONObject r = new JSONObject();
        r.put("name", "初始值");
        r.put("code", getCode());

        JSONArray list = new JSONArray();
        if (xml != null) for (Xml x : xml.getChildren())
        {
            String name = x.getAttribute("name");
            String param = x.getAttribute("param");
            String type = x.getAttribute("type");
            String value = x.getAttribute("value");
            String file = x.getAttribute("file");
            String formula = x.getAttribute("formula");
            String desc = x.getAttribute("desc");
            String text = x.getText();

            if (Common.isEmpty(value))
                value = text;

            JSONObject v = new JSONObject();
            v.put("name", name);
            v.put("param", param);
            v.put("type", type);
            v.put("value", value);
            v.put("desc", desc);

            if (!Common.isEmpty(file))
            {
                v.put("lock", true);
            }
            if (!Common.isEmpty(formula))
            {
                v.put("type", null);
                v.put("value", formula);
            }

            list.add(v);
        }

        r.put("form", list);
        return r;
    }

    @Override
    public Xml save(JSONObject form)
    {
        return xml;
    }

    @Override
    public String getCode()
    {
        return "init";
    }
}
