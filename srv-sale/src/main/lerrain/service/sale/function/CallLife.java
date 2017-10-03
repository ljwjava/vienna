package lerrain.service.sale.function;

import com.alibaba.fastjson.JSONObject;
import lerrain.service.common.ServiceMgr;
import lerrain.tool.formula.Factors;
import lerrain.tool.formula.Function;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by lerrain on 2017/5/19.
 */
@Service
public class CallLife implements Function
{
    @Autowired
    ServiceMgr lifeins;

    @Override
    public Object run(Object[] objects, Factors factors)
    {
        Map self = (Map)factors.get("self");

        JSONObject req = new JSONObject();
        req.put("content", objects[0]);
        req.put("opt", objects.length > 1 ? objects[1] : self.get("opt"));

        return lifeins.req("lifeins", "pack/perform.json", req).getJSONObject("content");
    }
}
