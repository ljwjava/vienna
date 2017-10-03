package lerrain.service.util;

import java.util.Map;

import com.alibaba.fastjson.JSONObject;

public class ServiceUtil
{
	public static boolean isSuccess(JSONObject rsp)
	{
		return "success".equals(rsp.get("result"));
	}
	
	public static <T> T contentOf(JSONObject rsp)
	{
		return (T)rsp.get("content");
	}
	
	public static <T> T contentOf(JSONObject rsp, String path)
	{
		Map<String, Object> c = contentOf(rsp);
		return (T)valueOf(c, path);
	}
	
	public static <T> T valueOf(Map<String, Object> rsp, String path)
	{
		return (T)rsp.get(path);
	}
}
