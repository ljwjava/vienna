package lerrain.service.sale.function;

import lerrain.tool.formula.Factors;
import lerrain.tool.formula.Function;
import org.springframework.stereotype.Service;

import java.util.Map;


/**
 * Created by lerrain on 2017/5/19.
 */
@Service
public class UrlParam implements Function
{
    @Override
    public Object run(Object[] objects, Factors factors)
    {
        Map<String, Object> map = (Map<String, Object>)objects[0];
        if (map == null)
            return null;

        String url = null;
        for (Map.Entry e : map.entrySet())
            url = (url == null ? "" : url + "&") + e.getKey() + "=" + e.getValue();

        return url;
    }
}
