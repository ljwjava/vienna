package lerrain.service.sale.pack;

import com.alibaba.fastjson.JSONObject;
import lerrain.service.common.ServiceMgr;
import lerrain.tool.formula.Factors;
import lerrain.tool.formula.Function;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class Lifeins implements Function
{
    @Autowired
    ServiceMgr serviceMgr;

    @Override
    public Object run(Object[] objects, Factors factors)
    {
        JSONObject json = new JSONObject();
        json.put("planId", factors.get("REFER_KEY"));
        json.put("script", objects[0]);
        json.put("with", objects.length >= 2 ? translate((PackIns)factors.get("PACK"), (Map)objects[1]) : null);

        JSONObject res = serviceMgr.req("lifeins", "plan/perform.json", json);
        if ("success".equals(res.getString("result")))
            return res.get("content");
        else
            throw new RuntimeException(res.getString("reason"));
    }

    public static Map translate(PackIns packIns, Map vals)
    {
        Map r = new HashMap();

        if (packIns.getInputForm() != null) for (InputField field : packIns.getInputForm())
        {
            if (field.getScope() != null) for (String scope : field.getScope())
            {
                Map v = (Map)r.get(scope);
                if (v == null)
                {
                    v = new HashMap();
                    r.put(scope, v);
                }

                String name = field.getName();
                Object value = vals.get(name);

                if (value != null)
                    v.put(name, PackUtil.translate(field.getType(), value));
            }
        }

        return r;
    }
}
