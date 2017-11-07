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

    public static void main(String[] str) throws Exception
    {
        JSONObject json = JSON.parseObject("{\"endDate\":\"2017-09-02 00:00:00\",\"mode\":\"summary\",\"startDate\":\"2017-09-01 00:00:00\",\"time\":\"modify\",\"type\":\"account\"}");
        new IybUtil().request("https://www.iyunbao.com/open/iyb/openapi/IybBizOpenDispatcher.do", "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQChNwVveOgmYOOw6RiEOmXgn2H+Obeb10vVrdNMk80PS3KeeiXG55DGyoK8SOgq9QEO20NMwnWap9GCRPIdaLREq0+uDAwNkX4ebCUvLPety5mFRHUdzoQvD1KxEl7Uh+OyhylDUpV2qXKBr1K1pfMqvgI1HMcV/goVhpnL2iLdbwIDAQAB", "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAIE2Pbwi54ShvxRTKAC75jadv5JrI8IzZNJKVTHx9HJSyRD5odvR3wtjtEcaYAEUazeMasRBPh6+tqCdEiY68VWylwIEsNqXSMbbSgcdSPABUhcKUqFSrYRbHjqgOkWy+GlLSbGOK4azzwMA4lJ0zZ5qZVAv9ALy946tdqoMD1HdAgMBAAECgYAoi7HBmJ5Xsz23jgSOfmfWGZgkxPP6m4/2oRaszoTrXujzJ7JPvUKlW0sVyMP5csPWMXzLSsHIegXqzn8EehiCXpxOUzKXSnR7lxsPQ5a5y2OoT0DKIrHCyuPWLLqsS67y3Gww+ICfzxjQygSZHhu0yIhf/qJMrMD29Sn0wt4IgQJBALZ4i+jmzm5rNoPnMXa6IWerl3HUkgOXvUgkeODun1LNEijQ1eOYK09enBfS9z4uE3MnJAn8kBYJAiK6696fjP0CQQC1R5EKXNUJr2rTwwF8Tu4LEvCKX/FD26NKHSi5q7ag9bf3M3dWJ/LqvIM1IH73+RKWYWHZrfZapuzEFXveDl5hAkB6FKbOuPUuDQ8ZKun+HEPTP4uAfg7+1luuU7CIFT6FQGzA4A/qSNnZLVxT2DLQ6DTdGzfScqbYB0jlgZfjh23lAkA7zWQ3uvA8xbXELiyrSY6V8KWRwJzi+e4gYQYyWkdhSi5qSnwv2/XzIkVWnWXFgFF1cqLJIO6IcVDiTwQR+UOBAkEAivs58l2pQN+Ex+F5Tl40EnuucDq37XGGcpBGAWiWpg7COpnaJKg8qeK5Mqd3Ue8+a1zxZ9NblwJlKAwDu6pMRw==", "iybTableSyncNotify", json);
    }
}
