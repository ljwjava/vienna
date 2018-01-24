package lerrain.service.env.function;

import lerrain.tool.CipherUtil;
import lerrain.tool.formula.Factors;
import lerrain.tool.formula.Function;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

public class Encrypt implements Factors
{
    Map<String, Object> map = new HashMap<>();

    public Encrypt()
    {
        map.put("sign", new Function() {
            @Override
            public Object run(Object[] objects, Factors factors)
            {
                try
                {
                    if (objects.length > 2 && "sofa".equals(objects[2].toString()))
                    {
                        return EncryptIybSofa.sign(objects[0].toString(), objects[1].toString());
                    }
                    else
                    {
                        return CipherUtil.encryptByPrivateKey(objects[0].toString().getBytes("UTF-8"), objects[1].toString());
                    }
                }
                catch (Exception e)
                {
                    throw new RuntimeException(e);
                }
            }
        });

        map.put("cpt", new Function() {
            @Override
            public Object run(Object[] objects, Factors factors)
            {
                try
                {
                    return CipherUtil.encryptByPublicKey(objects[0].toString().getBytes("UTF-8"), objects[1].toString());
                }
                catch (Exception e)
                {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Override
    public Object get(String s)
    {
        return map.get(s);
    }
}
