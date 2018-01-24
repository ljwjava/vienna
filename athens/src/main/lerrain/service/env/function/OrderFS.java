package lerrain.service.env.function;

import com.alibaba.fastjson.JSONObject;
import lerrain.service.common.ServiceMgr;
import lerrain.tool.formula.Factors;
import lerrain.tool.formula.Function;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lerrain on 2017/8/24.
 */
public class OrderFS implements Factors
{
    @Autowired
    ServiceMgr serviceMgr;

    Map<String, Object> map = new HashMap<>();

    public OrderFS()
    {
        map.put("save", new Function() {
            @Override
            public Object run(Object[] objects, Factors factors)
            {
                return serviceMgr.req("order", "save.json", (JSONObject)JSONObject.toJSON(objects[0])).getLong("content");
            }
        });

        map.put("load", new Function()
        {
            @Override
            public Object run(Object[] objects, Factors factors)
            {
                JSONObject req = new JSONObject();
                req.put("orderId", objects[0]);

                return serviceMgr.req("order", "view.json", req).getJSONObject("content");
            }
        });
    }

    @Override
    public Object get(String s)
    {
        return map.get(s);
    }
}
