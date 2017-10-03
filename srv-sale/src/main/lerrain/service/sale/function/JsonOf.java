package lerrain.service.sale.function;

import com.alibaba.fastjson.JSON;

import lerrain.tool.formula.Factors;
import lerrain.tool.formula.Function;

public class JsonOf implements Function
{
	@Override
	public Object run(Object[] v, Factors p)
	{
		if (v == null || v.length == 0)
			return null;
		
		Object val = v[0];

		if (val instanceof String)
		{
			try
			{
				return JSON.parseObject((String)val);
			}
			catch (Exception e1)
			{
				try
				{
					return JSON.parseArray((String)val);
				}
				catch (Exception e2)
				{
					return null;
				}
			}
		}
		else
		{
			return JSON.toJSON(val);
		}
	}
}
