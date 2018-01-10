package lerrain.service.sale;

import com.alibaba.fastjson.JSONObject;
import lerrain.service.common.ServiceMgr;
import lerrain.tool.Common;
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
        json.put("planId", factors.get("PLAN_ID"));
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

        Map app = new HashMap();
        app.put("GENDER", vals.get("A_GENDER"));
        app.put("BIRTHDAY", Common.dateOf(vals.get("A_BIRTHDAY")));

        r.put("applicant", app);
        r.put("RELATIVE", vals.get("RELATIVE"));

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

                Object value = vals.get(field.getName());
                if (value != null)
                    v.put(field.getVar(), PackUtil.translate(field.getType(), value));
            }
        }

        return r;
    }
}
