package lerrain.service.biz.function;

import lerrain.service.biz.KeyValService;
import lerrain.tool.formula.Factors;
import lerrain.tool.formula.Function;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 缓存打包值，返回key，以后可以根据key取回值，一般用于页面转到第三方页面，任务完成回调时。
 */
@Component
public class Unfold implements Function
{
    @Autowired
    KeyValService kvs;

    @Override
    public Object run(Object[] objects, Factors factors)
    {
        return kvs.getValue((String)objects[0]);
    }
}
