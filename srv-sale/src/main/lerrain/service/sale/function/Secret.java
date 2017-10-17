package lerrain.service.sale.function;

import com.alibaba.fastjson.JSONObject;
import lerrain.service.common.ServiceMgr;
import lerrain.tool.CipherUtil;
import lerrain.tool.formula.Factors;
import lerrain.tool.formula.Function;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class Secret implements Factors
{
    Map<String, Object> map = new HashMap<>();

    public Secret()
    {
        map.put("sign", new Function() {
            @Override
            public Object run(Object[] objects, Factors factors)
            {
                try
                {
                    return CipherUtil.encryptByPrivateKey(objects[0].toString().getBytes("UTF-8"), objects[1].toString());
                }
                catch (Exception e)
                {
                    throw new RuntimeException(e);
                }
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
