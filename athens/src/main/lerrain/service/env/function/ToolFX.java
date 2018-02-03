package lerrain.service.env.function;

import lerrain.tool.Common;
import lerrain.tool.formula.Factors;
import lerrain.tool.formula.Function;

import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by lerrain on 2017/9/19.
 */
public class ToolFX extends HashMap<String, Object>
{
    public ToolFX()
    {
        this.put("uuid", new Function()
        {
            @Override
            public Object run(Object[] objects, Factors factors)
            {
                return UUID.randomUUID().toString();
            }
        });
    }
}
