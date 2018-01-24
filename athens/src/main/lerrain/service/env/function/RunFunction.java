package lerrain.service.env.function;

import lerrain.tool.formula.Factors;
import lerrain.tool.formula.Function;
import org.springframework.stereotype.Component;

/**
 * 缓存打包值，返回key，以后可以根据key取回值，一般用于页面转到第三方页面，任务完成回调时。
 */
@Component
public class RunFunction implements Function
{
    @Override
    public Object run(Object[] objects, Factors factors)
    {
        return ((Function)objects[0]).run(objects.length >= 2 ? (Object[])objects[1] : null, objects.length >= 3 ? (Factors)objects[2] : factors);
    }
}
