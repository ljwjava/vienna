package lerrain.service.lifeins;

import java.util.*;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import lerrain.project.insurance.plan.Commodity;
import lerrain.project.insurance.plan.Input;
import lerrain.project.insurance.plan.Plan;
import lerrain.project.insurance.plan.SequenceList;
import lerrain.project.insurance.plan.filter.StaticText;
import lerrain.project.insurance.plan.filter.chart.Chart;
import lerrain.project.insurance.plan.filter.chart.ChartLine;
import lerrain.project.insurance.plan.filter.liability.Liability;
import lerrain.project.insurance.plan.filter.table.Blank;
import lerrain.project.insurance.plan.filter.table.Table;
import lerrain.project.insurance.product.Insurance;
import lerrain.project.insurance.product.Purchase;
import lerrain.project.insurance.product.attachment.coverage.Coverage;
import lerrain.project.insurance.product.attachment.coverage.CoverageParagraph;
import lerrain.project.insurance.product.rule.Rule;
import lerrain.project.insurance.product.rule.RuleUtil;
import lerrain.tool.Common;

public class LifeinsUtil
{
	public static JSONObject feeOf(Plan plan)
	{
		List<Commodity> detail = plan.getCommodityList().toList();

		int max = 0;
		for (Commodity c : detail)
		{
			if (c.getPay() != null)
			{
				int year = c.getPay().getPeriodYear();
				if (max < year)
					max = year;
			}
		}

		double[] v = new double[max];
		double[] f = new double[max];
		double[] r = new double[max];

		for (Commodity c : detail)
		{
			int year = c.getPay().getPeriodYear();
			for (int i=0; i<v.length && i<year; i++)
			{
				v[i] += c.getPremium(Commodity.PREMIUM_YEAR, i);
				r[i] += c.getCommission(Commodity.COMMISSION_RATE, i);
				f[i] += c.getCommission(Commodity.COMMISSION_YEAR, i);
			}
		}

		JSONObject res = new JSONObject();
		res.put("premium", v);
		res.put("commission", f);
		res.put("commissionRate", r);

		return res;
	}

	public static JSONObject toSaveJson(Plan plan)
	{
		JSONObject r = new JSONObject();

		r.put("planId", plan.getId());
		if (plan.getApplicant() != null)
			r.put("applicant", jsonOf((Customer)plan.getApplicant()));
		if (plan.getInsurant() != null)
			r.put("insurant", jsonOf((Customer)plan.getInsurant()));

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		r.put("product", list);

		double total = 0;

		List<Commodity> detail = plan.getCommodityList().toList();
		for (Commodity c : detail)
		{
			Map<String, Object> m = new HashMap<String, Object>();
			m.put("id", c.getId());
			m.put("productId", c.getProduct().getId());
			m.put("vendor", c.getCompany().getId());
			m.put("auto", c.isAuto());
			m.put("value", c.getValue());

			int index = detail.indexOf(c.getParent());
			m.put("parent", index < 0 ? null : index);

			double premium = c.getPremium();
			m.put("premium", premium);

			total += premium;
			list.add(m);
		}

		r.put("premium", total);

		return r;
	}

	public static Plan toPlan(LifeinsService lifeins, JSONObject saveJson)
	{
		String planId = saveJson.getString("planId");

		Plan plan = new Plan(customerOf(saveJson.getJSONObject("applicant")), customerOf(saveJson.getJSONObject("insurant")));
		plan.setId(Common.isEmpty(planId) ? Common.nextId("plan") : planId);

		if (saveJson.containsKey("product")) for (Object prd : saveJson.getJSONArray("product"))
		{
			JSONObject m = (JSONObject) prd;

			String productId = m.getString("productId");
			String vendor = m.getString("vendor");

			Integer index = m.getInteger("parent");
			Commodity parent = index == null ? null : plan.getCommodity(index);

			Insurance ins = lifeins.getCompany(vendor).getProduct(productId);

			final Commodity c = new Commodity(plan, parent, ins, null, null);
			c.setId(m.getString("id"));
			c.setAuto(m.getBooleanValue("auto"));

			JSONObject val = m.getJSONObject("value");
			if (val != null) for (Map.Entry<String, Object> entry : val.entrySet())
				c.setValue(entry.getKey(), entry.getValue());

			plan.getCommodityList().addCommodity(parent, c);
		}

		return plan;
	}

	public static JSONObject jsonOf(Plan plan)
	{
		JSONObject r = new JSONObject();

		r.put("planId", plan.getId());
		if (plan.getApplicant() != null)
			r.put("applicant", jsonOf((Customer)plan.getApplicant()));
		if (plan.getInsurant() != null)
			r.put("insurant", jsonOf((Customer)plan.getInsurant()));
		
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		r.put("product", list);
		
		double t1 = 0, t2 = 0;

		List<Commodity> detail = plan.getCommodityList().toList();
		for (Commodity c : detail)
		{
			Map<String, Object> m = mapOf(c, detail);

			List<Commodity> children = c.getChildren() == null ? null : c.getChildren().toList();
			if (children != null)
			{
				JSONArray ja = new JSONArray();
				for (Commodity child : children)
					ja.add(mapOf(child, children));
				m.put("children", ja);
			}

			list.add(m);

			try
			{
				t1 += c.getPremium();
				t2 += c.getCommission(Commodity.COMMISSION_TERM);
			}
			catch (Exception e)
			{
			}
		}

		List<String> ruleList = new ArrayList<String>();
		List<Rule> r1 = RuleUtil.check(plan);
		if (r1 != null)
		{
			for (Rule rule : r1)
				ruleList.add(rule.getDesc());
			r.put("rule", ruleList);
		}
		
		r.put("premium", String.format("%.2f", t1));
		r.put("commission", String.format("%.2f", t2));
		
		return r;
	}

	private static Map<String, Object> mapOf(Commodity c, List list)
	{
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("commodityId", c.getId());
		m.put("productId", c.getProduct().getId());
		m.put("vendor", c.getCompany().getId());
		m.put("code", c.getProduct().getCode());
		m.put("auto", c.isAuto());
		m.put("type", c.getType());
		m.put("productType", c.getProduct().getProductType());
		m.put("parent", c.getParent() == null ? null : list.indexOf(c.getParent()));
		m.put("name", c.getProduct().getName());
		m.put("abbrName", c.getProduct().getAbbrName());
		m.put("age", c.getFactor("AGE"));

		try
		{
			for (String type : (List<String>)c.getProduct().getOptionType())
			{
				Input input = c.getInput(type);
				m.put(type, input.getDesc());
				m.put(type + "_code", input.getCode());
				m.put(type + "_mode", input.getMode());
				m.put(type + "_value", input.getValue());
				m.put(type + "_period", input.getPeriodYear());
			}

			int purchase = c.getProduct().getPurchaseMode();
			if ((purchase & Purchase.AMOUNT) > 0)
				m.put("amount", c.getAmount());
			else if ((purchase & Purchase.QUANTITY) > 0)
				m.put("quantity", c.getQuantity());

			m.put("premium", c.getPremium());
			m.put("purchase", c.getAmountDesc());

			if (c.getProduct().getDutyList() != null)
			{
				JSONArray duty = new JSONArray();
				List l = (List)c.getFactor("DUTY");
				for (int i=0;i<l.size();i++)
				{
					duty.add(l.get(i));
				}
				m.put("duty", duty);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		List<String[]> d = new ArrayList<>();
		d.add(new String[] {"保费", String.format("%.2f", c.getPremium())});
		if (c.getPay() != null)
			d.add(new String[] {"缴费期间", c.getPay().getDesc()});
		if (c.getInsure() != null)
			d.add(new String[] {"保障期间", c.getInsure().getDesc()});
		int purchase = c.getProduct().getPurchaseMode();
		if (purchase == Purchase.AMOUNT)
			d.add(new String[] {"保额", c.getAmount() % 10000 < 0.01f ? Math.round(c.getAmount() / 10000) + "万" : c.getAmount() + "元"});
		else if (purchase == Purchase.QUANTITY)
			d.add(new String[] {"份数", c.getQuantity() + "份"});
		else if (purchase == Purchase.RANK)
			d.add(new String[] {"档次", c.getRank().getDesc()});
		m.put("descr", d);

		List<String> ruleList = new ArrayList<String>();
		List<Rule> r1 = RuleUtil.check(c);
		if (r1 != null)
		{
			for (Rule rule : r1)
				ruleList.add(rule.getDesc());
			m.put("rule", ruleList);
		}

		return m;
	}
	
	public static Customer customerOf(Map<String, Object> map)
	{
		if (map == null)
			return null;
		
		return customerOf(new Customer(), map);
	}
	
	public static Customer customerOf(Customer c, Map<String, Object> map)
	{
		if (map == null)
			return c;

		c.setId(Common.trimStringOf(map.get("id")));

		Object gender = map.get("gender");
		Object birthdayStr = map.get("birthday");
		
		Date birthday;
		if (birthdayStr == null)
		{
			int age = Common.intOf(map.get("age"), 0);
			birthday = Common.getDate(Calendar.getInstance().get(Calendar.YEAR) - age, 1, 1);
		}
		else
		{
			birthday = Common.dateOf(birthdayStr);
		}
		
		c.setGenderCode("M".equals(gender) ? Customer.GENDER_MALE : Customer.GENDER_FEMALE);
		c.setBirthday(birthday);
		c.setName((String)map.get("name"));
		
		return c;
	}

	
	public static JSONObject jsonOf(Customer c)
	{ 
		JSONObject r = new JSONObject();
		if (c.vals != null)
			r.putAll(c.vals);
		
		r.put("id", c.getId());
		r.put("name", c.getName());
		r.put("age", c.getAge());
		r.put("gender", c.getGenderCode() == Customer.GENDER_MALE ? "M" : (c.getGenderCode() == Customer.GENDER_FEMALE ? "F" : null));
		r.put("birthday", Common.getString(c.getBirthday()));
		
		return r;
	}
}
