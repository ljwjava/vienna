package lerrain.service.env.function;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lerrain.service.common.Log;
import lerrain.tool.CipherUtil;
import lerrain.tool.Common;
import lerrain.tool.Network;
import lerrain.tool.formula.Factors;
import lerrain.tool.formula.Function;

import java.util.HashMap;

/**
 * Created by lerrain on 2017/9/19.
 */
public class IYunBao extends HashMap<String, Object>
{
    public IYunBao()
    {
        this.put("open", new Function()
        {
            @Override
            public Object run(Object[] objects, Factors factors)
            {
                String url = Common.trimStringOf(factors.get("IYB_OPEN_URL"));
                String pubKey = Common.trimStringOf(factors.get("IYB_OPEN_PUBLIC_KEY"));
                String priKey = Common.trimStringOf(factors.get("IYB_OPEN_PRIVATE_KEY"));

                try
                {
                    return request(url, pubKey, priKey, objects[0].toString(), (JSON)JSON.toJSON(objects[1]));
                }
                catch (Exception e)
                {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private JSONObject request(String url, String publicKey, String privateKey, String serviceName, JSON req) throws Exception
    {
        String bizContent = req.toJSONString();
//        Log.debug("service << " + bizContent);

        byte[] encodedData = CipherUtil.encryptByPublicKey(bizContent.getBytes(), publicKey);
        bizContent = Common.encodeToString(encodedData);

        JSONObject paramObj = new JSONObject();
        paramObj.put("serviceName", serviceName); // iybOrderNotify or iybAccountInfoProvider
        paramObj.put("bizContent", bizContent);

        String res = Network.request(url, paramObj.toJSONString());
//        Log.debug("service >> " + res);
        String obj = JSONObject.parseObject(res).getString("bizContent");

        byte[] resData = decodeHex(obj.toCharArray());
        byte[] decodedData = CipherUtil.decryptByPrivateKey(resData, privateKey);
        String outputStr = new String(decodedData, "utf-8");

//        Log.debug("service >> " + outputStr);

        JSONObject resObj = JSONObject.parseObject(outputStr);

        if (resObj == null || !"success".equals(resObj.get("result")))
            throw new RuntimeException("fail");

        return resObj;
    }

    public static byte[] decodeHex(final char[] data)
    {
        final int len = data.length;

        if ((len & 0x01) != 0)
            throw new RuntimeException("Odd number of characters.");

        final byte[] out = new byte[len >> 1];

        // two characters form the hex value.
        for (int i = 0, j = 0; j < len; i++) {
            int f = toDigit(data[j], j) << 4;
            j++;
            f = f | toDigit(data[j], j);
            j++;
            out[i] = (byte) (f & 0xFF);
        }

        return out;
    }

    protected static int toDigit(final char ch, final int index)
    {
        final int digit = Character.digit(ch, 16);
        if (digit == -1)
            throw new RuntimeException("Illegal hexadecimal character " + ch + " at index " + index);

        return digit;
    }
}
