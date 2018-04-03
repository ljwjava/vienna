package lerrain.service.env.function;

import com.alibaba.fastjson.JSON;
import lerrain.tool.formula.Factors;
import lerrain.tool.formula.Function;

public class JsonStr implements Function
{
	@Override
	public Object run(Object[] v, Factors p)
	{
		return JSON.toJSONString(v[0]);
	}
}
