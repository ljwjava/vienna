package lerrain.service.env.function;

import lerrain.service.env.util.BASE64Encoder;
import lerrain.tool.CipherUtil;
import lerrain.tool.Common;
import lerrain.tool.formula.Factors;
import lerrain.tool.formula.Function;

import java.util.HashMap;
import java.util.Map;

public class Encrypt implements Factors
{
    Map<String, Object> map = new HashMap<>();
    BASE64Encoder encoder=new BASE64Encoder();

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

        map.put("base64EN", new Function() {
            @Override
            public Object run(Object[] v, Factors factors)
            {
                try {
//                    return Common.encodeBase64(v[0].toString().getBytes("UTF-8"));
                    return encoder.encode(v[0].toString().getBytes("UTF-8"));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });

        map.put("base64DE", new Function() {
            @Override
            public Object run(Object[] v, Factors factors)
            {
                try
                {
                    return Common.decodeBase64(v[0].toString());
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
