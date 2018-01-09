package lerrain.service.fee;

import com.alibaba.fastjson.JSONObject;
import lerrain.tool.Network;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class IybPushMsg
{
	@Value("${service.iyb}")
	String serviceIyb;

	String SERVER_URL = "/open/iyb/openapi/IybBizOpenDispatcher.do";

    String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQChNwVveOgmYOOw6RiEOmXgn2H+Obeb10vVrdNMk80PS3KeeiXG55DGyoK8SOgq9QEO20NMwnWap9GCRPIdaLREq0+uDAwNkX4ebCUvLPety5mFRHUdzoQvD1KxEl7Uh+OyhylDUpV2qXKBr1K1pfMqvgI1HMcV/goVhpnL2iLdbwIDAQAB";
    String privateKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAIE2Pbwi54ShvxRTKAC75jadv5JrI8IzZNJKVTHx9HJSyRD5odvR3wtjtEcaYAEUazeMasRBPh6+tqCdEiY68VWylwIEsNqXSMbbSgcdSPABUhcKUqFSrYRbHjqgOkWy+GlLSbGOK4azzwMA4lJ0zZ5qZVAv9ALy946tdqoMD1HdAgMBAAECgYAoi7HBmJ5Xsz23jgSOfmfWGZgkxPP6m4/2oRaszoTrXujzJ7JPvUKlW0sVyMP5csPWMXzLSsHIegXqzn8EehiCXpxOUzKXSnR7lxsPQ5a5y2OoT0DKIrHCyuPWLLqsS67y3Gww+ICfzxjQygSZHhu0yIhf/qJMrMD29Sn0wt4IgQJBALZ4i+jmzm5rNoPnMXa6IWerl3HUkgOXvUgkeODun1LNEijQ1eOYK09enBfS9z4uE3MnJAn8kBYJAiK6696fjP0CQQC1R5EKXNUJr2rTwwF8Tu4LEvCKX/FD26NKHSi5q7ag9bf3M3dWJ/LqvIM1IH73+RKWYWHZrfZapuzEFXveDl5hAkB6FKbOuPUuDQ8ZKun+HEPTP4uAfg7+1luuU7CIFT6FQGzA4A/qSNnZLVxT2DLQ6DTdGzfScqbYB0jlgZfjh23lAkA7zWQ3uvA8xbXELiyrSY6V8KWRwJzi+e4gYQYyWkdhSi5qSnwv2/XzIkVWnWXFgFF1cqLJIO6IcVDiTwQR+UOBAkEAivs58l2pQN+Ex+F5Tl40EnuucDq37XGGcpBGAWiWpg7COpnaJKg8qeK5Mqd3Ue8+a1zxZ9NblwJlKAwDu6pMRw==";

    private static final char[] DIGITS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	public String send(String accountId, String tabType, String msgType, String subTitle, String desc, String contentMsg, String imgUrl)
	{
		if(StringUtils.isEmpty(accountId)){
			System.out.println("推送消息失败，accountId不能为空");
			return null;
		}
		// 活动类型消息，必须要传所有数据
		if("act".equals(tabType)){
			if(StringUtils.isEmpty(imgUrl)){
				System.out.println("推送消息失败，活动类型消息缺少图片链接");
				return null;
			} else if(!"h5".equals(msgType)){
				System.out.println("推送消息失败，活动类型消息的消息类型须为H5");
				return null;
			}
		}

		JSONObject content = new JSONObject();
		content.put("accountId", accountId);	// 接收消息的accountId
		content.put("tabType", "act".equals(tabType) ? "1" : "sys".equals(tabType) ? "3" : "tips".equals(tabType) ? "2" : "2");	// 分类类型（1-活动[act]； 2-提醒[tips]； 3-系统消息[sys]）
		content.put("msgType", "h5".equals(msgType) ? "3" : "broad".equals(msgType) ? "2" : "group".equals(msgType) ? "1" : "self".equals(msgType) ? "0" : "0");	// 消息类型（0-个人消息[self]；1-组消息[group]；2-广播消息[broad]；3-H5页面[h5]）
		content.put("subTitle", subTitle);	// 子标题（列表的title）
		content.put("desc", desc);	// 描述信息（列表显示）
		content.put("content", contentMsg);	// 内容（可以是msg，当msgType=3时是url）
		if("act".equals(tabType)){
			content.put("imgUrl", imgUrl);	// H5页面展示图片（可空）
		}
		
		String bizContent = content.toJSONString(); 
		try
		{
	        byte[] encodedData = CipherUtil.encryptByPublicKey(bizContent.getBytes(), publicKey);
	        bizContent = encodeToString(encodedData);
	
			JSONObject req = new JSONObject();
			req.put("appKey", "app"); 
			req.put("bizContent", bizContent); 
			req.put("charset", "UTF-8"); 
			req.put("format", "json"); 
			req.put("serviceName", "iybPushMsgNotify");
			req.put("sign_type", "RSA"); 
			req.put("timestamp", System.currentTimeMillis()); 
			
			System.out.println("pushMsg - " + content);
			
			return Network.request(serviceIyb + SERVER_URL, req.toJSONString());
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
    public static String encodeToString(byte[] bytes) {
        char[] encodedChars = encode(bytes);
        return new String(encodedChars);
    }

    private static char[] encode(byte[] data) {
        int l = data.length;
        char[] out = new char[l << 1];
        for (int i = 0, j = 0; i < l; i++) {
            out[j++] = DIGITS[(0xF0 & data[i]) >>> 4];
            out[j++] = DIGITS[0x0F & data[i]];
        }
        return out;
    }

	public static void main(String[] args) {
//		PushMsg c = new PushMsg();
//		System.out.println(c.run(new Object[]{"50003300001", "act", "h5", "我要小粉", "小粉在哪里", "https://www.baidu.com", "https://gpo.iyunbao.com/static/activity/images/actzxplus/share_icon.png"}, null));
//		System.out.println(c.run(new Object[]{"50003300001", "act", "self", "我要小粉", "小粉在哪里", "https://www.baidu.com", "https://gpo.iyunbao.com/static/activity/images/actzxplus/share_icon.png"}, null));
//		System.out.println(c.run(new Object[]{"50003300001", "act", "self", "我要小粉", "小粉在哪里", "https://www.baidu.com", null}, null));
//		System.out.println(c.run(new Object[]{"50003300001", "act", "self", "我要小粉", "小粉在哪里", "https://www.baidu.com", null}, null));
//		System.out.println(c.run(new Object[]{"50003300001", "tips", "h5", "我要小粉", "小粉在哪里", "https://www.baidu.com", "https://gpo.iyunbao.com/static/activity/images/actzxplus/share_icon.png"}, null));
//		System.out.println(c.run(new Object[]{"50003300001", "tips", "self", "我要小粉", "小粉在哪里", "https://www.baidu.com", "https://gpo.iyunbao.com/static/activity/images/actzxplus/share_icon.png"}, null));
//		System.out.println(c.run(new Object[]{"50003300001", "sys", "h5", "我要小粉", "小粉在哪里", "https://www.baidu.com", "https://gpo.iyunbao.com/static/activity/images/actzxplus/share_icon.png"}, null));
//		System.out.println(c.run(new Object[]{"50003300001", "sys", "self", "我要小粉", "小粉在哪里", "https://www.baidu.com", "https://gpo.iyunbao.com/static/activity/images/actzxplus/share_icon.png"}, null));
//		System.out.println(c.run(new Object[]{"50001555001", "sys", "self", "我要小粉", "小粉在哪里", "https://www.baidu.com", "https://gpo.iyunbao.com/static/activity/images/actzxplus/share_icon.png"}, null));
//		System.out.println(c.run(new Object[]{"50001555001,50001565002", "act", "self", "我要小粉", "小粉在哪里", "https://www.baidu.com", "https://gpo.iyunbao.com/static/activity/images/actzxplus/share_icon.png"}, null));

		JSONObject jo = (JSONObject) JSONObject.parse("{}");
		String s = jo.get("aaa") + "123";
		System.out.println(s);
		s = jo.getString("aaa") + "123";
		System.out.println(s);
	}
}
