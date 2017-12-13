package lerrain.service.sale.pack;

import com.alibaba.fastjson.JSONObject;
import lerrain.service.common.ServiceMgr;
import lerrain.tool.Common;
import lerrain.tool.formula.Formula;
import lerrain.tool.script.Stack;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;


@Service
public class PackService
{
	@Autowired
	PackDao packDao;

	@Autowired
	ServiceMgr serviceMgr;

	Map<Object, PackIns> packs;

	public void reset()
	{
		packs = packDao.loadPacks();
	}

	public PackIns getPack(Long packId)
	{
		return packs.get(packId);
	}

	public PackIns getPack(String packCode)
	{
		return packs.get(packCode);
	}

	private Stack factorsOf(PackIns packIns, Map<String, Object> vals)
	{
		if (packIns.getInputForm() != null) for (InputField field : packIns.getInputForm())
		{
			String name = field.getName();
			Object value = vals.get(name);

			if (value == null)
				continue;

			vals.put(name, PackUtil.translate(field.getType(), value));
		}

		Stack stack = new Stack(packIns.getStack());
		stack.declare("self", vals);

		return stack;
	}

	public Object perform(Formula opt, PackIns packIns, Map<String, Object> vals)
	{
		Stack stack = factorsOf(packIns, vals);
		return opt.run(stack);
	}

	public Map<String, Object> getPrice(PackIns packIns, Map<String, Object> vals)
	{
		Map<String, Object> r = new HashMap<>();

		if (packIns.getPriceType() == PackIns.PRICE_FIXED)
		{
			r.put("total", packIns.getPrice());
		}
		else if (packIns.getPriceType() == PackIns.PRICE_FACTORS)
		{
			StringBuffer key = null;

			if (packIns.getPrice() != null) for (String f : (String[])packIns.getPrice())
			{
				key = new StringBuffer();
				Object val = vals.get(f);

				if (val == null)
				{
					for (InputField field : packIns.getInputForm())
						if (field.getVar().equals(f))
							val = field.getValue();
				}

				if (val == null)
				{
					key = null;
					break;
				}

				key.append(val.toString());
				key.append(",");
			}

			Double rate = packDao.getPackRate(packIns, key == null ? null : key.toString());
			if (rate != null)
			{
				Double total = rate * Common.doubleOf(vals.get("QUANTITY"), 1);
				r.put("total", total);
			}
		}
		else if (packIns.getPriceType() == PackIns.PRICE_OTHER)
		{
			JSONObject json = new JSONObject();
			json.put("planId", packIns.getReferKey());
			json.put("with", Lifeins.translate(packIns, vals));

			JSONObject res = serviceMgr.req("lifeins", "plan/perform.json", json);
			r.put("total", res.get("premium"));
		}

		r.put("premium", r.get("total")); //兼容

		return r;
	}
}
