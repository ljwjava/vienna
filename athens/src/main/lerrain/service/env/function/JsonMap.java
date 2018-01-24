package lerrain.service.env.function;

import com.alibaba.fastjson.JSONObject;
import lerrain.tool.formula.Factors;
import lerrain.tool.formula.Function;

public class JsonMap implements Function
{
	@Override
	public Object run(Object[] v, Factors p)
	{
		return new JSONObject();
	}
}
