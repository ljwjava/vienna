package lerrain.service.customer;

import com.alibaba.fastjson.JSONObject;
import lerrain.tool.Common;

public class CustomerTool
{
    public static JSONObject jsonOf(Customer c)
    {
        JSONObject v = new JSONObject();
        if (c.getDetail() != null)
            v.putAll(c.getDetail());

        v.put("id", c.getId());
        v.put("name", c.getName());
        v.put("gender", c.getGender());
        v.put("birthday", Common.getString(c.getBirthday()));
        v.put("certType", c.getCertType());
        v.put("certNo", c.getCertNo());

        return v;
    }
}
