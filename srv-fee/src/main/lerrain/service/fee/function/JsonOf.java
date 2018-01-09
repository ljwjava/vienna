package lerrain.service.fee.function;

import com.alibaba.fastjson.JSON;
import lerrain.tool.formula.Factors;
import lerrain.tool.formula.Function;

public class JsonOf implements Function
{
    @Override
    public Object run(Object[] objects, Factors factors)
    {
        return JSON.toJSON(objects[0]);
    }
}
