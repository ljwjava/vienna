package lerrain.service.env.function;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lerrain.service.common.Log;
import lerrain.tool.CipherUtil;
import lerrain.tool.Common;
import lerrain.tool.Network;
import lerrain.tool.formula.Factors;
import lerrain.tool.formula.Function;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

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
                try
                {
                    return request(factors, objects[0].toString(), (JSON)JSON.toJSON(objects[1]));
                }
                catch (Exception e)
                {
                    throw new RuntimeException(e);
                }
            }
        });

        this.put("pay", new Function()
        {
            @Override
            public Object run(Object[] v, Factors factors)
            {
                try
                {
                    return pay(factors, Common.toLong(v[0]), Common.intOf(v[1], 3), Common.doubleOf(v[2], 0), (Map)v[3]);
                }
                catch (Exception e)
                {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    /**
     *
     * @param factors
     * @param userId
     * @param type
     * @param amt
     * @param m
     * message
     * bizId
     * bizNo
     * productId
     * productName
     * premium
     * parentId
     * delay
     *
     * @return
     */
    public Object pay(Factors factors, Long userId, int type, double amt, Map m)
    {
        String productName = (String)m.get("productName");

        JSONObject content = new JSONObject();
        content.put("accountId", userId);
        content.put("bizNo", m.get("bizNo"));	// 保单号
        content.put("activityCode", Common.isEmpty(productName) ? (type == 1 ? "佣金" : type == 2 ? "间接佣金" : "奖金") : productName);
        content.put("effectiveDays", Common.intOf(m.get("delay"), 3));
        content.put("amount", amt);
        content.put("activityDetail", null);
        content.put("messageTitle", m.get("message"));
        content.put("commType", "D");	// 默认直佣

        Long bizId = Common.toLong(m.get("bizId"));
        Long productId = Common.toLong(m.get("bizId"));
        double premium = Common.doubleOf(m.get("premium"), 0);

        if (bizId != null && bizId > 0)
            content.put("bizId", bizId);	// 保单ID
        if (productId != null && productId > 0)
            content.put("productId", productId);	// 保单对应产品
        if (premium > 0)
            content.put("premium", (new BigDecimal(premium)).setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString());	// 保单对应保费

        try
        {
            // 直佣/间佣 需关联保单号、产品等信息
            if (type == 2 || type == 1)
            {
                if (bizId == null || bizId <= 0) // 保单ID
                    throw new RuntimeException("佣金发送失败，佣金需提供保单ID[policyId]");
                if (productId == null || productId <= 0) // 保单对应产品
                    throw new RuntimeException("佣金发送失败，佣金需提供产品ID[productId]");
                if (premium > 0)
                    content.put("premium", (new BigDecimal(premium)).setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString());	// 保单对应保费
                if (type == 2) // 间佣
                {
                    Long fromUserId = Common.toLong(m.get("parentId"));
                    if (fromUserId == null || fromUserId <= 0)
                        throw new RuntimeException("佣金发送失败，间佣需提供来源账户ID[fromUserId]");

                    content.put("sourceAccountId", fromUserId);	// 来源ID（下线）
                    content.put("commType", "I");	// I-间佣； D-直佣
                }
            }

            return request(factors, "iybCommissionPayNotify", content);
        }
        catch (Exception e)
        {
            Log.error(e);
            return null;
        }
    }

    private JSONObject request(Factors factors, String serviceName, JSON req) throws Exception
    {
        String url = Common.trimStringOf(factors.get("IYB_OPEN_URL"));
        String publicKey = Common.trimStringOf(factors.get("IYB_OP EN_PUBLIC_KEY"));
        String privateKey = Common.trimStringOf(factors.get("IYB_OPEN_PRIVATE_KEY"));

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
