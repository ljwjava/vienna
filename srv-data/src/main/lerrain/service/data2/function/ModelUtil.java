package lerrain.service.data2.function;

import lerrain.tool.formula.Factors;
import lerrain.tool.formula.Function;

import java.util.HashMap;

/**
 * Created by lerrain on 2017/9/19.
 */
public class ModelUtil extends HashMap<String, Object>
{
    public ModelUtil()
    {
        this.put("loadByTag", new Function()
        {
            @Override
            public Object run(Object[] objects, Factors factors)
            {
                return null;
            }
        });
    }
}
