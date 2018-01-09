package lerrain.service.lifeins;

import com.alibaba.fastjson.JSONObject;
import lerrain.project.insurance.plan.Commodity;
import lerrain.project.insurance.plan.Plan;
import lerrain.project.insurance.product.Company;
import lerrain.project.insurance.product.Insurance;
import lerrain.project.insurance.product.rule.Rule;
import lerrain.project.insurance.product.rule.RuleUtil;
import lerrain.service.common.Log;
import lerrain.tool.Common;
import lerrain.tool.formula.Factors;
import lerrain.tool.formula.Formula;
import lerrain.tool.formula.Function;
import lerrain.tool.script.Stack;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PlanService
{
	@Autowired
	PlanDao2 pd;

	@Autowired
	PlanDao pdo;

	@Autowired
	LifeinsService lifeins;

	Stack env;

	Cache cache;

	public void reset()
	{
		pd.supplyClauses();

		env = initEnv();
		cache = new Cache(env);

		Map<String, Object> list = pd.loadAll();
		if (list != null)
			for (Map.Entry<String, Object> m : list.entrySet())
				if (m != null)
					cache.put(m.getKey(), m.getValue());
	}

	private Stack initEnv()
	{
		Map<String, Function> functions = new HashMap<>();

		functions.put("getPlan", new Function()
		{
			@Override
			public Object run(Object[] objects, Factors factors)
			{
				return getPlan(objects[0].toString());
			}
		});

		functions.put("newPlan", new Function()
		{
			@Override
			public Object run(Object[] objects, Factors factors)
			{
				if (objects != null && objects.length > 0)
					((Stack)factors).set("vendor", objects[0]);

				Plan plan = new Plan(new Customer(), new Customer());
				return plan;
			}
		});

		functions.put("newClause", new Function()
		{
			@Override
			public Object run(Object[] objects, Factors factors)
			{
				Plan plan = (Plan)objects[0];
				String clauseId = (String)objects[1];

				String vendor = (String)factors.get("vendor");
				Insurance ins = vendor == null ? lifeins.getProduct(clauseId) : lifeins.getCompany(vendor).getProduct(clauseId);

				Commodity parent = null;
				if (objects.length > 2)
					parent = (Commodity)objects[2];

				if (parent == null)
					return plan.newCommodity(ins);
				else
					return plan.newCommodity(parent, ins);
			}
		});

		functions.put("setValue", new Function()
		{
			@Override
			public Object run(Object[] objects, Factors factors)
			{
				Commodity c = (Commodity)objects[0];
				c.setValue(objects[1].toString(), objects[2]);
				return c;
			}
		});

		functions.put("check", new Function()
		{
			@Override
			public Object run(Object[] objects, Factors factors)
			{
				return getRule((Plan)objects[0]);
			}
		});

		functions.put("getPremium", new Function()
		{
			@Override
			public Object run(Object[] objects, Factors factors)
			{
				return getPremium((Plan) objects[0]);
			}
		});

		functions.put("getChart", new Function()
		{
			@Override
			public Object run(Object[] objects, Factors factors)
			{
				return LifeinsShow.formatChart((Commodity) objects[0]);
			}
		});

		functions.put("getTable", new Function()
		{
			@Override
			public Object run(Object[] objects, Factors factors)
			{
				return LifeinsShow.formatTable((Commodity) objects[0], Common.boolOf(objects[1], false));
			}
		});

		functions.put("getProduct", new Function()
		{
			@Override
			public Object run(Object[] objects, Factors factors)
			{
				Object v1 = objects[1];
				if (v1 instanceof String)
					return ((Plan)objects[0]).getCommodityByProductId(v1.toString());
				else
					return ((Plan)objects[0]).getCommodity(Common.intOf(v1, -1));
			}
		});

		return new Stack(functions);
	}

	public Object perform(String planId, Formula f, Map vals)
	{
		Stack stack = new Stack(env);
		stack.declare("self", vals);

		Plan plan = getPlan(planId, stack);
		reset(plan, vals);

		if (f == null)
			return LifeinsUtil.jsonOf(plan);

		stack.declare("plan", plan);
		stack.declare("PLAN", plan.getFactors());
		return f.run(stack);
	}

	public Plan getPlan(String planId)
	{
		return getPlan(planId, null);
	}

	public Plan getPlan(String planId, Factors f)
	{
		if (planId == null)
			return null;

		Plan plan = cache.get(planId, f);

		if (plan == null)
		{
			try
			{
				plan = pdo.load(planId);
			}
			catch (Exception e)
			{
				Log.alert("load " + planId + " fail - " + e.toString());
				plan = null;
			}

			if (plan == null)
				throw new RuntimeException("已过期，请重新打开该计划");

			cache.put(plan.getId(), plan);
		}

		return plan;
	}

	public void newPlan(Plan plan)
	{
		cache.put(plan.getId(), plan);
	}

	public List<Insurance> getRecommend(Plan plan)
	{
		List<Insurance> r = new ArrayList<>();

		if (plan.isEmpty())
			return r;

		Company company = plan.primaryCommodity().getCompany();
		if ("iyb".equals(company.getId()))
		{
			if (plan.getCommodityByProductId("IYB00002") == null)
			{
				Insurance ins = company.getProduct("IYB00002");
				if (ins != null)
					r.add(ins);
			}
		}

		return r;
	}

	private List<String> getRule(Plan plan)
	{
		List<String> rules = new ArrayList<>();

		for (int i=0;i<plan.size();i++)
		{
			Commodity c = plan.getCommodity(i);

			List<Rule> rs = null;
			try
			{
				rs = RuleUtil.check(c);
				if (rs != null) for (Rule rule : rs)
					rules.add(c.getProduct().getAbbrName() + "：" + rule.getDesc().trim());
			}
			catch (Exception e)
			{
				rules.add(c.getProduct().getAbbrName() + "规则校验错误");
			}
		}

		try
		{
			List<Rule> rs = RuleUtil.check(plan);
			if (rs != null) for (Rule rule : rs)
				rules.add(rule.getDesc().trim());
		}
		catch (Exception e)
		{
			rules.add("通则校验错误");
		}

		return rules.isEmpty() ? null : rules;
	}

	private double getPremium(Plan plan)
	{
		double total = 0;

		for (int i=0;i<plan.size();i++)
		{
			Commodity c = plan.getCommodity(i);

			try
			{
				total += c.getPremium();
			}
			catch (Exception e)
			{
				return -1;
			}
		}

		return total;
	}

	private void reset(Plan plan, Map<String, Object> stack)
	{
		Date date = Common.dateOf(stack.get("EFFECTIVE_DATE"));
		if (date != null)
			plan.setInsureTime(date);

		Customer applicant = (Customer)plan.getApplicant();
		applicant.setAge(30);

		String relation = (String)stack.get("RELATION");
		boolean forSelf = "self".equals(relation);

		Customer insurant = forSelf ? applicant : new Customer();
		insurant.set("RELATION", relation);

		plan.setInsurant(insurant);

		for (Map.Entry<String, Object> scp : stack.entrySet())
		{
			String v = scp.getKey();
			Object vals = scp.getValue();

			if (vals instanceof Map) for (Map.Entry<String, Object> m : ((Map<String, Object>)vals).entrySet())
			{
				Object value = m.getValue();
				String var = m.getKey();

				if ("insurant".equals(v)) {
					if ("AGE".equals(var))
						insurant.setAge(Common.intOf(value, 0));
					else if ("BIRTHDAY".equals(var))
						insurant.setBirthday(Common.dateOf(value));
					else if ("GENDER".equals(var))
						insurant.setGenderCode("M".equals(value) ? Customer.GENDER_MALE : "F".equals(value) ? Customer.GENDER_FEMALE : Customer.GENDER_UNKNOW);
					else
						insurant.set(var, value);
				} else if ("applicant".equals(v) && !forSelf) {
					if ("AGE".equals(var))
						applicant.setAge(Common.intOf(value, 0));
					else if ("BIRTHDAY".equals(var))
						applicant.setBirthday(Common.dateOf(value));
					else if ("GENDER".equals(var))
						applicant.setGenderCode("M".equals(value) ? Customer.GENDER_MALE : "F".equals(value) ? Customer.GENDER_FEMALE : Customer.GENDER_UNKNOW);
					else
						applicant.set(var, value);
				} else if ("primary".equals(v)) {
					Commodity c = plan.getCommodity(0);
					c.setValue(var, value);
				} else if (v != null) {
					Commodity c = plan.getCommodityByProductId(v);
					if (c != null)
						c.setValue(var, value);
				}
			}
		}

		plan.clearCache();
	}

	public class Cache
	{
		Factors factors;

		Map<Object, Object> cache = new HashMap<>();

		public Cache(Factors factors)
		{
			this.factors = factors;
		}

		public void put(Object id, Object value)
		{
			synchronized (cache)
			{
				cache.put(id, value);
			}
		}

		public <T> T get(Object id, Factors f)
		{
			synchronized (cache)
			{
				Object val = cache.get(id);
				if (val instanceof Formula)
					val = ((Formula)val).run(f != null ? f : factors);

				return (T)val;
			}
		}
	}
}
