package lerrain.service.lifeins.format;

import lerrain.project.insurance.plan.Commodity;
import lerrain.project.insurance.plan.Plan;
import lerrain.project.insurance.plan.filter.FilterPlan;
import lerrain.tool.Common;
import lerrain.tool.formula.Formula;
import lerrain.tool.formula.Value;
import lerrain.tool.script.Script;
import lerrain.tool.script.Stack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FGraphFilter implements FilterPlan
{
	static List<Category> list;
	static String[] vars = "COMMON_MEDICAL,CANCER_MEDICAL,ACCIDENT_DISABILITY,OTHER_DISABILITY,MILD,MILD_EXEMPT,ACCIDENT_DEATH,OTHER_DEATH,PAYBACK_DEATH".split(",");

	static
	{
		list = new ArrayList<>();
		list.add(new Category("医疗保障", "COMMON_MEDICAL + CANCER_MEDICAL", new double[] {10000,50000,100000,1000000},
			new Item("一般医疗保险金", "'因意外伤害或非等待期因患疾病，在二级及以上医院普通部发生需个人支付，必须且合理的住院或特殊门诊费用最高可报销'+format('%.2f', COMMON_MEDICAL/10000)+'万。'", "COMMON_MEDICAL > 0"),
			new Item("癌症医疗保险金", "'罹患癌症，在二级及以上医院普通部发生需个人支付，必须且合理的住院或特殊门诊费用最高可报销'+format('%.2f', CANCER_MEDICAL/10000)+'万。无免赔。'", "COMMON_MEDICAL > 0")
		));
		list.add(new Category("伤残保障", "max(ACCIDENT_DISABILITY, OTHER_DISABILITY, TOTAL_DISABILITY[1])", new double[] {100000,500000,1000000,2000000},
			new Item("全残保障", "'若被保人不幸全残，给付全残保险金，最高首年'+format('%.2f', TOTAL_DISABILITY[0]/10000)+'万，次年及以后'+format('%.2f', TOTAL_DISABILITY[1]/10000)+'万。'", "TOTAL_DISABILITY != null && TOTAL_DISABILITY[0] > 0")
		));
		list.add(new Category("重疾保障", "max(THUNDER[0], THUNDER[1])", new double[] {100000,200000,500000,1000000},
			new Item(null, "'罹患所保障的重大疾病，保险公司给付重疾保险金，最高首年'+format('%.2f', THUNDER[0]/10000)+'万，次年及以后'+format('%.2f', THUNDER[1]/10000)+'万。'", "THUNDER != null && THUNDER[0] > 0")
		));
		list.add(new Category("轻症保障", "MILD", new double[] {20000,50000,100000,200000},
			new Item(null, "'罹患所保障的轻症疾病，保险公司给付轻症保险金最高'+format('%.2f', MILD/10000)+'万。'", "MILD > 0"),
			new Item("轻症豁免", "'罹患所保障的轻症疾病，免缴剩余保费'", "MILD_EXEMPT > 0")
		));
		list.add(new Category("身故保障", "max(ACCIDENT_DEATH, OTHER_DEATH)", new double[] {100000,500000,1000000,2000000},
			new Item("意外身故", "'被保人因意外伤害原因导致身故，保险公司给付将身故保险金，最高为'+format('%.2f', ACCIDENT_DEATH/10000)+'万。'", "ACCIDENT_DEATH > 0"),
			new Item("非意外身故", "'若被保人因非意外伤害原因导致身故，保险公司给付身故保险金，最高为'+format('%.2f', OTHER_DEATH/10000)+'万。'", "OTHER_DEATH > 0"),
			new Item("身故返还", "'被保人身故，保险公司给付身故保险金，为和谐健康之享主险已交保费'", "PAYBACK_DEATH > 0")
		));
	}

	private Object addUp(Object v1, Object v2)
	{
		if ((v1 instanceof Object[]) || (v2 instanceof Object[]))
		{
			if (v1 == null)
				return v2;
			else if (v2 == null)
				return v1;

			Object[] o1 = (Object[])v1;
			Object[] o2 = (Object[])v2;
			Object[] o3 = new Object[Math.max(o1.length, o2.length)];
			for (int i = 0; i < o3.length; i++)
				o3[i] = Common.doubleOf(i < o1.length ? o1[i] : 0, 0) + Common.doubleOf(i < o2.length ? o2[i] : 0, 0);

			return o3;
		}
		else
		{
			return Common.doubleOf(v1, 0) + Common.doubleOf(v2, 0);
		}
	}

	@Override
	public Object filtrate(Plan plan, String attachmentName)
	{
		List res = new ArrayList();

		Map map = new HashMap();
		for (String var : "COMMON_MEDICAL,CANCER_MEDICAL,ACCIDENT_DISABILITY,OTHER_DISABILITY,MILD,MILD_EXEMPT,ACCIDENT_DEATH,OTHER_DEATH,PAYBACK_DEATH".split(","))
			map.put(var, 0);

		for (int i=0;i<plan.size();i++)
		{
			Commodity c = plan.getCommodity(i);
			Map<String, Formula> items = (Map)c.getProduct().getAttachment(attachmentName);
			if (items != null) for (Map.Entry<String, Formula> item : items.entrySet())
			{
				Object val = item.getValue().run(c.getFactors());
				map.put(item.getKey(), addUp(map.get(item.getKey()), val));
			}
		}

		Stack stack = new Stack(map);

		for (Category ct : list)
		{
			List detail = new ArrayList();
			if (ct.vars != null) for (Item item : ct.vars)
			{
				if (item.c == null || Value.booleanOf(item.c, stack))
				{
					Map d = new HashMap();
					d.put("name", item.name);
					d.put("text", Value.stringOf(item.f, stack));
					detail.add(d);
				}
			}

			if (!detail.isEmpty())
			{
				double amt = Value.doubleOf(ct.f, stack);

				int rank = ct.ranks.length;
				for (int i=0;i<ct.ranks.length;i++)
				{
					if (ct.ranks[i] > amt)
					{
						rank = i;
						break;
					}
				}

				Map cm = new HashMap();
				cm.put("name", ct.name);
				cm.put("amount", amt);
				cm.put("rank", rank);
				cm.put("detail", detail);
				res.add(cm);
			}
		}
		
		return res;
	}

	static class Category
	{
		String name;
		Formula f;

		double[] ranks;
		Item[] vars;

		public Category(String name, String f, double[] ranks, Item... vars)
		{
			this.name = name;
			this.f = Script.scriptOf(f);
			this.ranks = ranks;
			this.vars = vars;
		}
	}

	static class Item
	{
		String name;
		Formula f, c;

		public Item(String name, String f)
		{
			this(name, f, null);
		}

		public Item(String name, String f, String c)
		{
			this.name = name;
			this.f = Script.scriptOf(f);
			this.c = Script.scriptOf(c);
		}
	}
}
