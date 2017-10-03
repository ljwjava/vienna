package lerrain.service.sale.function;

import lerrain.service.common.ServiceMgr;
import lerrain.service.sale.KeyValService;
import lerrain.tool.formula.Factors;
import lerrain.tool.formula.Function;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 缓存打包值，返回key，以后可以根据key取回值，一般用于页面转到第三方页面，任务完成回调时。
 */
@Component
public class Fold implements Function
{
    @Autowired
    KeyValService kvs;

    @Override
    public Object run(Object[] objects, Factors factors)
    {
        String key = UUID.randomUUID().toString();
        kvs.setValue(key, objects[0]);

        return key;
    }
}
