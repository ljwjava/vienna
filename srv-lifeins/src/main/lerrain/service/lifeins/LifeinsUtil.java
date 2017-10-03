package lerrain.service.lifeins;

import java.util.*;

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
		plan.setId(Common.isEmpty(planId) ? Common.nextId() : planId);

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
		
		String smoke = (String)map.get("smoke");
		c.set("SMOKE", "Y".equals(smoke) ? 2 : 1);
		
		return c;
	}

	public static JSONArray formatTable(Plan plan)
	{
		JSONArray r = new JSONArray();
		return r;
	}
	
	public static JSONArray formatLiability(Commodity c)
	{
		Liability coverage = (Liability)c.format("liability");

		JSONArray l2 = new JSONArray();

		for (int k=0;k<coverage.size();k++)
		{
			Liability cp = coverage.getParagraph(k);

			JSONObject m3 = new JSONObject();
			m3.put("title", cp.getTitle());

			JSONArray l3 = new JSONArray();
			m3.put("content", l3);

			for (int l=0;l<cp.size();l++)
			{
				Liability cc = cp.getParagraph(l);
				if (cc.getContent() instanceof String)
				{
					JSONObject m4 = new JSONObject();
					m4.put("text", cc.getContent());
					m4.put("type", "text");

					l3.add(m4);
				}
				else if (cc.getContent() instanceof Table)
				{
					l3.add(tableOf((Table)cc.getContent(), false));
				}
			}

			l2.add(m3);
		}

		return l2;
	}

	public static JSONArray formatCoverage(Commodity c)
	{
		Coverage coverage = (Coverage)c.format("coverage");

		JSONArray l2 = new JSONArray();

		for (int k=0;k<coverage.getParagraphCount();k++)
		{
			CoverageParagraph cp = coverage.getParagraph(k);

			JSONObject m3 = new JSONObject();
			m3.put("title", cp.getTitle());

			JSONArray l3 = new JSONArray();
			m3.put("content", l3);

			for (int l=0;l<cp.size();l++)
			{
				if (cp.getType(l) == CoverageParagraph.TEXT)
				{
					JSONObject m4 = new JSONObject();
					m4.put("text", cp.getContent(l));
					m4.put("type", "text");

					l3.add(m4);
				}
				else if (cp.getType(l) == CoverageParagraph.TABLE)
				{
					l3.add(tableOf((Table)cp.getContent(l), false));
				}
			}

			l2.add(m3);
		}

		return l2;
	}

	public static JSONObject formatChart(Commodity c)
	{
		JSONObject r = new JSONObject();

		List<Chart> list = (List<Chart>)c.format("benefit_chart");
		Chart chart = list == null || list.isEmpty() ? null : list.get(0);

		int age = Common.intOf(c.getFactor("AGE"), 0);

		r.put("name",  c.getProduct().getName());
		r.put("age", age);

		int b = chart.getStart();
		int e = chart.getEnd();
		int s = chart.getStep();

		List s1 = new ArrayList<>();
		List s3 = new ArrayList<>();
		String[] s2 = new String[(e-b)/s+1];

		for (int i=0;i<chart.size();i++)
		{
			ChartLine line = chart.getLine(i);

			double[] v = new double[(e-b)/s+1];
			int cc=0;
			for (int j=b;j<=e;j+=s)
				v[cc++] = line.getData()[j];

			Map m = new HashMap<>();
			m.put("name", line.getName());
			m.put("type", line.getType()==ChartLine.TYPE_BAR ? "bar" : "line");
			m.put("data", v);
			s1.add(m);
			s3.add(line.getName());
		}

		int cc=0;
		for (int j=b;j<=e;j+=s)
		{
			s2[cc++] = (j+age) +"岁";
		}

		r.put("data", s1);	// value
		r.put("axis", s2);	// 年龄
		r.put("sets", s3);	// name

		return r;
	}


	public static JSONArray formatTable(Commodity c, boolean fold)
	{
		JSONArray r = new JSONArray();

		List<Object> list = (List)c.format("benefit_table");
		if (list != null) for (Object line : list)
		{
			if (line instanceof Table)
			{
				r.add(tableOf((Table)line, fold));
			}
			else if (line instanceof StaticText)
			{
				StaticText st = (StaticText)line;
				Map json = new HashMap();
				json.put("text", st.getText());
				json.put("bold", st.getStyle("bold"));
				r.add(json);
			}
		}

		return r;
	}
	
	private static JSONObject tableOf(Table table, boolean fold)
	{
		Map<String, String>[][] h = new Map[table.getTitleHeight()][table.getMaxCol()];
		for (int i=0;i<table.getTitleHeight();i++)
		{
			for (int j=0;j<table.getMaxCol();j++)
			{
				Blank blank = table.getTitleBlank(i, j);
				if (blank != null && blank.getText() != null)
				{
					h[i][j] = new HashMap<String, String>();
					h[i][j].put("row", blank.getRowspan() + "");
					h[i][j].put("col", blank.getColspan() + "");
					h[i][j].put("text", blank.getText());
				}
			}
		}

		List<String[]> m = new ArrayList<>();
		for (int i=0;i<table.getMaxRow();i++)
		{
			if (fold && i >= 10 && i % 5 != 4 && i != table.getMaxRow() - 1)
				continue;

			String[] mm = new String[table.getMaxCol()];
			for (int j=0;j<table.getMaxCol();j++)
			{
				Blank blank = table.getBlank(i, j);
				if (blank != null)
				{
					mm[j] = blank.getText();
				}
			}
			m.add(mm);
		}

		JSONObject json = new JSONObject();
		json.put("head", h);
		json.put("body", m);
		json.put("type", "table");
		json.put("name", table.getName());
		json.put("desc", table.getAdditional("desc"));
		
		return json;
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
