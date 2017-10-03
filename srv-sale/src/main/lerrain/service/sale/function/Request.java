package lerrain.service.sale.function;

import com.alibaba.fastjson.JSONObject;
import lerrain.tool.Common;
import lerrain.tool.formula.Factors;
import lerrain.tool.formula.Function;
import org.springframework.stereotype.Service;


/**
 * Created by lerrain on 2017/5/19.
 */
@Service
public class Request implements Function
{
    @Override
    public Object run(Object[] objects, Factors factors)
    {
        int time = objects.length > 2 ? Common.intOf(objects[2], 10000) : 10000;
        String method = objects.length > 3 ? ("GET".equalsIgnoreCase(Common.trimStringOf(objects[3])) ? "GET" : "POST") : "POST";
        return JSONObject.parseObject(RequestPost.request((String) objects[0], JSONObject.toJSONString(objects[1]), time, method));
    }

}
