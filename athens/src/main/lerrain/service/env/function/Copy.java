package lerrain.service.env.function;

import lerrain.tool.formula.Factors;
import lerrain.tool.formula.Function;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Copy implements Function
{
    @Override
    public Object run(Object[] objects, Factors factors)
    {
        return copy(objects[0]);
    }

    private static Object copy(Object v)
    {
        if (v instanceof List)
        {
            List r = new ArrayList();
            for (Object o : (List)v)
                r.add(copy(o));

            return r;
        }
        else if (v instanceof Map)
        {
            Map r = new HashMap();
            for (Map.Entry<?, ?> e : ((Map<?, ?>)v).entrySet())
                r.put(e.getKey(), copy(e.getValue()));

            return r;
        }
        else
        {
            return v;
        }
    }
}
