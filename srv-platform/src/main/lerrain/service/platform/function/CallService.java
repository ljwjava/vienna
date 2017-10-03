package lerrain.service.platform.function;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lerrain.service.common.ServiceMgr;
import lerrain.tool.formula.Factors;
import lerrain.tool.formula.Function;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CallService implements Function
{
    @Autowired
    ServiceMgr serviceMgr;

    @Override
    public Object run(Object[] objects, Factors factors)
    {
        JSONObject res = serviceMgr.req((String)objects[0], (String)objects[1], (JSON)JSONObject.toJSON(objects[2]));
        if (!"success".equals(res.getString("result")))
            throw new RuntimeException(res.getString("reason"));

        return res.get("content");
    }
}
