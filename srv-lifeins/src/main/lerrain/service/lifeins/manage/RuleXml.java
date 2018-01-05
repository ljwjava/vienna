package lerrain.service.lifeins.manage;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lerrain.tool.Common;

public class RuleXml implements InsPart
{
    Xml xml;

    public RuleXml(Xml xml)
    {
        this.xml = xml;
    }

    @Override
    public JSONObject toJson()
    {
        JSONArray ja = new JSONArray();
        for (Xml x : xml.getChildren())
        {
            JSONObject m = new JSONObject();
            m.put("rule", x.getAttribute(new String[] {"condition", "c"}));
            m.put("type", x.getAttribute("type"));
            m.put("level", x.getAttribute("level"));
            m.put("text", Common.trimStringOf(x.getText()));
            ja.add(m);
        }

        JSONObject r = new JSONObject();
        r.put("name", "投保规则");
        r.put("code", getCode());
        r.put("type", "rule");
        r.put("value", ja);

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
        return "rule";
    }
}
