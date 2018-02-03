package lerrain.service.env.function;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lerrain.service.common.ServiceMgr;
import lerrain.tool.formula.Factors;
import lerrain.tool.formula.Function;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Dict implements Function
{
    @Autowired
    ServiceMgr serviceMgr;

    @Override
    public Object run(Object[] objects, Factors factors)
    {
        JSONObject prm = new JSONObject();
        prm.put("company", objects[0]);
        prm.put("name", objects[1]);

        JSONObject res = serviceMgr.req("dict", "view.json", prm);
        if (!"success".equals(res.getString("result")))
            throw new RuntimeException(res.getString("reason"));

        return res.get("content");
    }
}
