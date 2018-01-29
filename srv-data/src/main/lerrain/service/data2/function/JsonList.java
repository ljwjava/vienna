package lerrain.service.data2.function;

import com.alibaba.fastjson.JSONArray;
import lerrain.tool.formula.Factors;
import lerrain.tool.formula.Function;

public class JsonList implements Function
{
	@Override
	public Object run(Object[] v, Factors p)
	{
		return new JSONArray();
	}
}
