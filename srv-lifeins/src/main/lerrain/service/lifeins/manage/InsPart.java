package lerrain.service.lifeins.manage;

import com.alibaba.fastjson.JSONObject;

public interface InsPart
{
    public JSONObject toJson();

    public Xml save(JSONObject form);

    public String getCode();
}
