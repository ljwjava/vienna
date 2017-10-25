package lerrain.service.commission;

import lerrain.service.common.Log;
import lerrain.tool.Network;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;

@Component
public class IybPay
{
    @Value("${service.iyb}")
    String serviceIyb;
	
	String SERVER_URL = "/open/iyb/openapi/IybBizOpenDispatcher.do";

    String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQChNwVveOgmYOOw6RiEOmXgn2H+Obeb10vVrdNMk80PS3KeeiXG55DGyoK8SOgq9QEO20NMwnWap9GCRPIdaLREq0+uDAwNkX4ebCUvLPety5mFRHUdzoQvD1KxEl7Uh+OyhylDUpV2qXKBr1K1pfMqvgI1HMcV/goVhpnL2iLdbwIDAQAB";
    String privateKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAIE2Pbwi54ShvxRTKAC75jadv5JrI8IzZNJKVTHx9HJSyRD5odvR3wtjtEcaYAEUazeMasRBPh6+tqCdEiY68VWylwIEsNqXSMbbSgcdSPABUhcKUqFSrYRbHjqgOkWy+GlLSbGOK4azzwMA4lJ0zZ5qZVAv9ALy946tdqoMD1HdAgMBAAECgYAoi7HBmJ5Xsz23jgSOfmfWGZgkxPP6m4/2oRaszoTrXujzJ7JPvUKlW0sVyMP5csPWMXzLSsHIegXqzn8EehiCXpxOUzKXSnR7lxsPQ5a5y2OoT0DKIrHCyuPWLLqsS67y3Gww+ICfzxjQygSZHhu0yIhf/qJMrMD29Sn0wt4IgQJBALZ4i+jmzm5rNoPnMXa6IWerl3HUkgOXvUgkeODun1LNEijQ1eOYK09enBfS9z4uE3MnJAn8kBYJAiK6696fjP0CQQC1R5EKXNUJr2rTwwF8Tu4LEvCKX/FD26NKHSi5q7ag9bf3M3dWJ/LqvIM1IH73+RKWYWHZrfZapuzEFXveDl5hAkB6FKbOuPUuDQ8ZKun+HEPTP4uAfg7+1luuU7CIFT6FQGzA4A/qSNnZLVxT2DLQ6DTdGzfScqbYB0jlgZfjh23lAkA7zWQ3uvA8xbXELiyrSY6V8KWRwJzi+e4gYQYyWkdhSi5qSnwv2/XzIkVWnWXFgFF1cqLJIO6IcVDiTwQR+UOBAkEAivs58l2pQN+Ex+F5Tl40EnuucDq37XGGcpBGAWiWpg7COpnaJKg8qeK5Mqd3Ue8+a1zxZ9NblwJlKAwDu6pMRw==";

	public String pay(Long userId, int type, int mode, double bonus, String productName, int day, String messageTitle, Long bizId, String bizNo, Long fromUserId, Long productId, double premium)
	{
		JSONObject content = new JSONObject();
		content.put("accountId", userId);
		content.put("bizNo", bizNo);	// 保单号
		content.put("activityCode", StringUtils.isEmpty(productName) ? (type == 1 ? "佣金" : type == 2 ? "间接佣金" : "奖金") : productName);
		content.put("effectiveDays", day);
		content.put("amount", bonus);
		content.put("activityDetail", null);
		content.put("messageTitle", messageTitle);
		content.put("commType", "D");	// 默认直佣

		// 直佣/间佣 需关联保单号、产品等信息
		if(type == 2 || type == 1){
			if(bizId != null && bizId > 0){
				content.put("bizId", bizId);	// 保单ID
			}else{
				System.out.println("佣金发送失败，佣金需提供保单ID[policyId]");
				return null;
			}
			if(productId != null && productId > 0){
				content.put("productId", productId);	// 保单对应产品
			}else{
				System.out.println("佣金发送失败，佣金需提供产品ID[productId]");
				return null;
			}
			if(premium > 0){
				content.put("premium", (new BigDecimal(premium)).setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString());	// 保单对应保费
			}
			if(type == 2){	// 间佣
				if(fromUserId != null && fromUserId > 0){
					content.put("sourceAccountId", fromUserId);	// 来源ID（下线）
					content.put("commType", "I");	// I-间佣； D-直佣
				}else{
					System.out.println("佣金发送失败，间佣需提供来源账户ID[fromUserId]");
					return null;
				}
			}
		}

		String bizContent = content.toJSONString();

		try
		{
	        byte[] encodedData = CipherUtil.encryptByPublicKey(bizContent.getBytes(), publicKey);
	        bizContent = CipherUtil.encodeToString(encodedData);
	
			JSONObject req = new JSONObject(); 
			req.put("appKey", "app"); 
			req.put("bizContent", bizContent); 
			req.put("charset", "UTF-8"); 
			req.put("format", "json"); 
			req.put("serviceName", "iybCommissionPayNotify");//iybOrderNotify or iybAccountInfoProvider 
			req.put("sign_type", "RSA"); 
			req.put("timestamp", System.currentTimeMillis()); 

			String res = Network.request(serviceIyb + SERVER_URL, req.toJSONString());
			Log.info(serviceIyb+">>> request:"+content.toJSONString()+" <<< response:"+res);
			return res;
		}
		catch (Exception e)
		{
			Log.error(e);
			return null;
		}
	}
}
