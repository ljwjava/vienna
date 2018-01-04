package lerrain.service.lifeins.manage;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class DataXml implements InsPart
{
    Xml xml;

    public DataXml(Xml xml)
    {
        this.xml = xml;
    }

    @Override
    public JSONObject toJson()
    {
        JSONObject r = new JSONObject();
        r.put("name", "外挂数据");
        r.put("code", getCode());
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
        return "data";
    }
}
