package lerrain.service.env.function;

import com.alibaba.fastjson.JSONObject;
import lerrain.tool.Common;
import lerrain.tool.Network;
import lerrain.tool.formula.Factors;
import lerrain.tool.formula.Function;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PushMsg implements Function
{
    @Value("${service.message}")
    String serviceMessage;
	String SERVER_URL = "/open/v1/messagerequest/request";


	@Override
	public Object run(Object[] v, Factors p)
	{
	    Object accountId = v[0];
	    String title = (String)v[1];
	    String subtitle = (String)v[2];
		int messageTabType = Common.intOf(v[3], 2);
		int messageContentType = Common.intOf(v[4], 0);
		int deliveryTimeType = Common.intOf(v[5], 1);
		int deliveryToType = Common.intOf(v[6], 1);
		boolean needPushMessage = Common.boolOf(v[7], true);
		String content = (String)v[8];
		String bizType = (String)v[9];
		String bizDesc = (String)v[10];
		String imageUrl = (String)v[11];

        JSONObject request = new JSONObject();
        request.put("accountId", accountId);
        request.put("title", title);
        request.put("subtitle", subtitle);
        request.put("messageTabType", messageTabType);
        request.put("messageContentType", messageContentType);
        request.put("deliveryTimeType", deliveryTimeType);
        request.put("deliveryToType", deliveryToType);
        request.put("needPushMessage", needPushMessage);
        request.put("messageContent", content);
        request.put("bizType", bizType);
        request.put("bizDesc", bizDesc);
        request.put("imageUrl", imageUrl);
        
		String bizContent = request.toJSONString();
		try
		{
			System.out.println("PushMsg - " + bizContent);
			return Network.request(serviceMessage + SERVER_URL, bizContent);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}
	
}
