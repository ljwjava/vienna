package lerrain.service.data.function;

import com.alibaba.fastjson.JSONObject;
import lerrain.tool.Common;
import lerrain.tool.formula.Factors;
import lerrain.tool.formula.Function;

public class Request implements Function
{
    @Override
    public Object run(Object[] objects, Factors factors)
    {
        int time = objects.length > 2 ? Common.intOf(objects[2], 10000) : 10000;
        return JSONObject.parseObject(RequestPost.request((String) objects[0], JSONObject.toJSONString(objects[1]), time));
    }

}
