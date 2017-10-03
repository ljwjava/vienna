package lerrain.service.sale.function;

import com.alibaba.fastjson.JSONObject;
import lerrain.service.common.ServiceMgr;
import lerrain.tool.formula.Factors;
import lerrain.tool.formula.Function;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class IybFunc implements Factors
{
    @Autowired
    ServiceMgr serviceMgr;

    Map<String, Object> map = new HashMap<>();

    public IybFunc()
    {
        map.put("saveOrder", new Function() {
            @Override
            public Object run(Object[] objects, Factors factors)
            {
                return null;
            }
        });

        map.put("savePolicy", new Function()
        {
            @Override
            public Object run(Object[] objects, Factors factors)
            {
                return null;
            }
        });

        map.put("apply", new Function()
        {
            @Override
            public Object run(Object[] objects, Factors factors)
            {
                return null;
            }
        });
    }

    @Override
    public Object get(String s)
    {
        return map.get(s);
    }
}
