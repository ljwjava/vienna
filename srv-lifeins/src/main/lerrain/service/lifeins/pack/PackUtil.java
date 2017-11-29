package lerrain.service.lifeins.pack;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lerrain.tool.Common;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by lerrain on 2017/5/5.
 */
public class PackUtil
{
    public static JSONObject toJson(PackIns packIns)
    {
        JSONObject r = new JSONObject();
        r.put("id", packIns.getId());
        r.put("code", packIns.getCode());
        r.put("name", packIns.getName());
        r.put("type", packIns.getType());
        r.put("vendor", packIns.getVendor());
        r.put("applyMode", packIns.getApplyMode());

        JSONArray factors = new JSONArray();
        r.put("factors", factors);

        for (InputField field : packIns.getInputForm())
            factors.add(field);

        r.put("docs", packIns.getDocs());

        return r;
    }

    public static Object translate(String type, Object value)
    {
        if ("boolean".equals(type))
            return Common.boolOf(value, false);
        else if ("integer".equals(type))
            return Common.intOf(value, 0);
        else if ("string".equals(type))
            return value == null ? null : value.toString();
        else if ("date".equals(type))
        {
            if ("today".equals(value))
                return new Date();
            else if ("tomorrow".equals(value))
                return new Date(new Date().getTime() + 3600000L * 24);
            else if ("yesterday".equals(value))
                return new Date(new Date().getTime() - 3600000L * 24);
            else
                return Common.dateOf(value);
        }
        else
            return value;
    }
}
