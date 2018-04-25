package lerrain.service.sale;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lerrain.tool.Common;
import lerrain.tool.formula.Value;
import lerrain.tool.script.Stack;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lerrain on 2017/5/5.
 */
public class PackUtil
{
    public static JSONObject toJson(PackIns packIns, JSONObject w)
    {
        JSONObject r = new JSONObject();
        r.put("id", packIns.getId());
        r.put("code", packIns.getCode());
        r.put("name", packIns.getName());
        r.put("type", packIns.getType());
        r.put("vendor", packIns.getVendor());
        r.put("applyMode", packIns.getApplyMode());
        r.put("formOpt", packIns.getFormOpt());

        r.put("wareId", packIns.getWare().getId());
        r.put("wareCode", packIns.getWare().getCode());
        r.put("wareName", packIns.getWare().getName());
        r.put("form", toForm(packIns, w));

        r.put("extra", packIns.getExtra());
        r.put("show", packIns.getShow());

        return r;
    }

    public static JSONArray toForm(PackIns packIns, JSONObject w)
    {
        JSONArray factors = new JSONArray();

        Map with = w;
        if (with == null)
            with = new HashMap<>();

        Stack stack = new Stack(with);
        synchronized (packIns)
        {
            for (InputField field : packIns.getInputForm())
            {
                if (!packIns.isDynamicForm() || field.getCondition() == null || Common.boolOf(field.getCondition().run(stack), false))
                {
                    JSONObject json = new JSONObject();
                    json.put("name", field.getName());
                    json.put("var", field.getVar());
                    json.put("label", field.getLabel());
                    json.put("type", field.getType());
                    //json.put("widget", !packIns.isDynamicForm() || field.getCondition() == null || Common.boolOf(field.getCondition().run(stack), false) ? field.getWidget() : "hidden");
                    json.put("scope", field.getScope());
                    json.put("detail", field.getDetail() == null ? null : field.getDetail().run(stack));
                    json.put("value", field.getValue());
                    factors.add(json);
                }

                if (w == null)
                    stack.set(field.getVar(), field.getValue());
            }
        }

        return factors;
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
