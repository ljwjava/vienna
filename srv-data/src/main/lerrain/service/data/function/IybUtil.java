package lerrain.service.data.function;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lerrain.service.common.Log;
import lerrain.tool.CipherUtil;
import lerrain.tool.Common;
import lerrain.tool.Network;
import lerrain.tool.formula.Factors;
import lerrain.tool.formula.Function;
import org.apache.commons.codec.binary.Hex;

import javax.crypto.Cipher;
import java.util.HashMap;

/**
 * Created by lerrain on 2017/9/19.
 */
public class IybUtil extends HashMap<String, Object>
{
    public IybUtil()
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
        Log.debug("service << " + bizContent);

        byte[] encodedData = CipherUtil.encryptByPublicKey(bizContent.getBytes(), publicKey);
        bizContent = Common.encodeToString(encodedData);

        JSONObject paramObj = new JSONObject();
        paramObj.put("serviceName", serviceName); // iybOrderNotify or iybAccountInfoProvider
        paramObj.put("bizContent", bizContent);

        String res = Network.request(url, paramObj.toJSONString());
        String obj = JSONObject.parseObject(res).getString("bizContent");

        byte[] resData = Hex.decodeHex(obj.toCharArray());
        byte[] decodedData = CipherUtil.decryptByPrivateKey(resData, privateKey);
        String outputStr = new String(decodedData, "utf-8");

        Log.debug("service >> " + outputStr);

        JSONObject resObj = JSONObject.parseObject(outputStr);

        if (resObj == null || !"success".equals(resObj.get("result")))
            throw new RuntimeException("fail");

        return resObj;
    }
}
