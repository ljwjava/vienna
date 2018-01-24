package lerrain.service.env.function;

import lerrain.tool.formula.Factors;
import lerrain.tool.formula.Function;
import lerrain.tool.script.Stack;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 缓存打包值，返回key，以后可以根据key取回值，一般用于页面转到第三方页面，任务完成回调时。
 */
@Component
public class StackOf implements Function
{
    @Override
    public Object run(Object[] objects, Factors factors)
    {
        if (objects[0] instanceof Factors)
            return new Stack((Factors)objects[0]);
        else if (objects[0] instanceof Map)
            return new Stack((Map)objects[0]);

        return new Stack();
    }
}
