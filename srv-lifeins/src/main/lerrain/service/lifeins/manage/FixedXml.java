package lerrain.service.lifeins.manage;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class FixedXml implements InsPart
{
    Xml xml;

    String code;
    String name;

    public FixedXml(Xml xml, String code, String name)
    {
        this.xml = xml.firstChild(code);
        this.code = code;
        this.name = name;
    }

    @Override
    public JSONObject toJson()
    {
        JSONObject r = new JSONObject();
        r.put("code", code);
        r.put("name", name);
        r.put("type", "fixed");

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
        return code;
    }
}
