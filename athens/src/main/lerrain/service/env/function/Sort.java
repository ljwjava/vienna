package lerrain.service.env.function;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import lerrain.tool.Common;
import lerrain.tool.formula.Factors;
import lerrain.tool.formula.Function;

public class Sort implements Function
{
	@Override
	public Object run(Object[] v, Factors p)
	{
		if (v[0] instanceof Map) //map 給key排序只能是<String, Object>
		{
			LinkedHashMap<Object, Object> res = new LinkedHashMap<>();
			Map<?, ?> src = (Map<?, ?>)v[0];
			
			List<String> list = new ArrayList<>();
			for (Object key : src.keySet())
				list.add(key == null ? null : key.toString());
			Collections.sort(list);
			
			for (Object key : list)
				res.put(key, src.get(key));
			
			return res;
		}
		
		boolean down = true;
		Object key = null;
		
		if (v.length >= 3)
		{
			down = (Boolean)v[2];
		}
		if (v.length >= 2)
		{
			key = v[1];
		}
		
		JSONArray list = new JSONArray();
		
		Object[] val = v[0] instanceof Object[] ? (Object[])v[0] : new Object[] {v[0]};
		for (Object obj : val)
		{
			if (obj instanceof List)
			{
				list.addAll((List<?>)obj);
			}
			else if (obj instanceof Object[])
			{
				for (Object o : (Object[])obj)
					list.add(o);
			}
			else if (obj instanceof double[])
			{
				for (double o : (double[])obj)
					list.add(o);
			}
			else if (obj instanceof int[])
			{
				for (int o : (int[])obj)
					list.add(o);
			}
			else if (obj instanceof BigDecimal[])
			{
				for (BigDecimal o : (BigDecimal[])obj)
					list.add(o);
			}
			else
			{
				list.add(obj);
			}
		}
		
		Collections.sort(list, new SortComparator(key, down));

		return list;
	}
	
	private class SortComparator implements Comparator<Object>
	{
		String key = null;
		int index = -1;
		
		boolean down;
		
		public SortComparator(Object key, boolean down)
		{
			if (key == null) 
			{}
			else if (key instanceof String)
				this.key = (String)key;
			else
				this.index = Common.intOf(key, 0);
			
			this.down = down;
		}

		@Override
		public int compare(Object o1, Object o2)
		{
			Object p1, p2;
			
			if (key == null && index < 0)
			{
				p1 = o1;
				p2 = o2;
			}
			else if (key != null)
			{
				if (o1 instanceof Factors)
					p1 = ((Factors)o1).get(key);
				else if (o1 instanceof Map)
					p1 = ((Map<?, ?>)o1).get(key);
				else
					p1 = o1;
				
				if (o2 instanceof Factors)
					p2 = ((Factors)o2).get(key);
				else if (o2 instanceof Map)
					p2 = ((Map<?, ?>)o2).get(key);
				else
					p2 = o2;
			}
			else
			{
				p1 = valueOf(o1, index);
				p2 = valueOf(o2, index);
			}
			
			if (p1 instanceof String && p2 instanceof String)
				return ((String)p1).compareTo((String)p2) * (down ? -1 : 1);
			
			double x1 = Common.doubleOf(p1, 0);
			double x2 = Common.doubleOf(p2, 0);
			
			int r = x1 > x2 ? 1 : (x2 > x1 ? -1 : 0);
			r = down ? r : -r;
			
//			System.out.println(r);
			
			return r;
		}
		
	}
	
	private Object valueOf(Object o1, int index)
	{
		Object p1;
		
		if (o1 instanceof List)
		{
			p1 = ((List<?>)o1).get(index);
		}
		else if (o1 instanceof Object[])
		{
			p1 = ((Object[])o1)[index];
		}
		else if (o1 instanceof double[])
		{
			p1 = ((double[])o1)[index];
		}
		else if (o1 instanceof int[])
		{
			p1 = ((int[])o1)[index];
		}
		else if (o1 instanceof BigDecimal[])
		{
			p1 = ((BigDecimal[])o1)[index];
		}
		else
		{
			p1 = o1;
		}
		
		return p1;
	}
	
	public static void main(String[] s)
	{
//		Object[] r1 = new Object[]{"12321", 5, 10};
//		Object[] r2 = new Object[]{"12344421", 5, 9};
//		JSONArray ja = new JSONArray();
//		ja.add(r1);
//		ja.add(r2);
//		
//		Object res = new Sort().run(new Object[]{ja, 2, true}, null);
//		System.out.println(res);
		
		Map m = new HashMap();
		m.put("fffC", "1213123");
		m.put("fffA", "1213123");
		m.put("fffX", "1213123");
		m.put("fffY", "1213123");
		
		Sort sss = new Sort();
		System.out.println(JSONObject.parseObject(JSONObject.toJSON(sss.run(new Object[]{m}, null)).toString()));
	}
}
