package lerrain.service.lifeins;

import lerrain.project.insurance.plan.Commodity;
import lerrain.project.insurance.plan.Plan;
import lerrain.project.insurance.product.Insurance;
import lerrain.project.insurance.product.rule.Rule;
import lerrain.project.insurance.product.rule.RuleUtil;
import lerrain.tool.Common;
import lerrain.tool.formula.Factors;
import lerrain.tool.formula.Formula;
import lerrain.tool.formula.Function;
import lerrain.tool.script.Stack;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ScriptService
{
	@Autowired
	PlanDao planDao;

	@Autowired
	LifeinsService lifeins;

	@Autowired
	PlanService planSrv;

	Map<Object, Formula> map;

	Stack env;

	public void reset()
	{
		env = initEnv();

		map = planDao.loadAllScript();
	}

	private Stack initEnv()
	{
		Map<String, Function> functions = new HashMap<>();

		functions.put("getPlan", new Function()
		{
			@Override
			public Object run(Object[] objects, Factors factors)
			{
				return planSrv.getPlan(objects[0].toString());
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

		functions.put("verify", new Function()
		{
			@Override
			public Object run(Object[] objects, Factors factors)
			{
				return verify((Plan)objects[0]);
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

//	public Object perform(List prds, String opt, Map vals)
//	{
//		Stack stack = new Stack(env);
//		stack.declare("self", vals);
//
//		Plan plan = new Plan(new Customer(), new Customer());
//		synchronized (plan)
//		{
//			String prdId = prds.get(0).toString();
//			Commodity main = plan.newCommodity(lifeins.getProduct(prdId));
//
//			for (int i = 1; i < prds.size(); i++)
//				plan.newCommodity(main, lifeins.getProduct(prds.get(i).toString()));
//
//			reset(plan, vals);
//
//			if (opt == null)
//				return LifeinsUtil.jsonOf(plan);
//
//			stack.declare("plan", plan);
//			stack.declare("PLAN", plan.getFactors());
//
//			Formula f = map.get("1/" + opt);
//			return f.run(stack);
//		}
//	}

	public Object perform(Long scriptId, List prds, String opt, Map vals)
	{
		Stack stack = new Stack(env);
		stack.declare("self", vals);

		Plan plan = getPlan(scriptId, prds, stack);
		synchronized (plan)
		{
			reset(plan, vals);

			if (opt == null)
				return LifeinsUtil.jsonOf(plan);

			stack.declare("plan", plan);
			stack.declare("PLAN", plan.getFactors());

			Formula f = map.get(scriptId + "/" + opt);
			if (f == null)
				f = map.get("1/" + opt);

			return f.run(stack);
		}
	}

	public Plan getPlan(Long scriptId, List prds, Factors f)
	{
		Formula s = scriptId == null ? null : map.get(scriptId);

		if (s == null)
		{
			Plan plan = new Plan(new Customer(), new Customer());

			String prdId = prds.get(0).toString();
			Commodity main = plan.newCommodity(lifeins.getProduct(prdId));

			for (int i = 1; i < prds.size(); i++)
				plan.newCommodity(main, lifeins.getProduct(prds.get(i).toString()));

			return plan;
		}
		else synchronized (s)
		{
			return (Plan) s.run(f);
		}
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

	private List<String>[] verify(Plan plan)
	{
		List<String> rules1 = new ArrayList<>();
		List<String> rules2 = new ArrayList<>();

		for (int i=0;i<plan.size();i++)
		{
			Commodity c = plan.getCommodity(i);

			try
			{
				List<Rule> rs = RuleUtil.check(c);
				if (rs != null) for (Rule rule : rs)
				{
					if (rule.getLevel() == Rule.LEVEL_FAIL)
						rules1.add(c.getProduct().getAbbrName() + "：" + rule.getDesc().trim());
					else if (rule.getLevel() == Rule.LEVEL_ALERT)
						rules2.add(c.getProduct().getAbbrName() + "：" + rule.getDesc().trim());
				}
			}
			catch (Exception e)
			{
				rules1.add(c.getProduct().getAbbrName() + "规则校验错误");
			}
		}

		try
		{
			List<Rule> rs = RuleUtil.check(plan);
			if (rs != null) for (Rule rule : rs)
			{
				if (rule.getLevel() == Rule.LEVEL_FAIL)
					rules1.add(rule.getDesc().trim());
				else if (rule.getLevel() == Rule.LEVEL_ALERT)
					rules2.add(rule.getDesc().trim());
			}
		}
		catch (Exception e)
		{
			rules1.add("通则校验错误");
		}

		return new List[] {rules1.isEmpty() ? null : rules1, rules2.isEmpty() ? null : rules2};
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
		{
			plan.setInsureTime(date);
		}
		else
		{
			int days = Common.intOf(stack.get("EFFECTIVE_DAYS"), 1);

			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.add(Calendar.DAY_OF_MONTH, days);

			plan.setInsureTime(cal.getTime());
		}

		Customer applicant = (Customer)plan.getApplicant();
		applicant.setAge(30);

		String relation = (String)stack.get("RELATIVE");
		boolean forSelf = "self".equals(relation);

		Customer insurant = forSelf ? applicant : new Customer();
		insurant.set("RELATIVE", relation);

		plan.setInsurant(insurant);

		for (Map.Entry<String, Object> scp : stack.entrySet())
		{
			String v = scp.getKey();
			Object vals = scp.getValue();

			if (vals instanceof Map) for (Map.Entry<String, Object> m : ((Map<String, Object>)vals).entrySet())
			{
				Object value = m.getValue();
				String var = m.getKey();

				if ("insurant".equals(v))
				{
					if ("AGE".equals(var))
						insurant.setAge(Common.intOf(value, 0));
					else if ("BIRTHDAY".equals(var))
						insurant.setBirthday(Common.dateOf(value));
					else if ("GENDER".equals(var))
						insurant.setGenderCode("M".equals(value) ? Customer.GENDER_MALE : "F".equals(value) ? Customer.GENDER_FEMALE : Customer.GENDER_UNKNOW);
					else
						insurant.set(var, value);
				}
				else if ("applicant".equals(v) && !forSelf)
				{
					if ("AGE".equals(var))
						applicant.setAge(Common.intOf(value, 0));
					else if ("BIRTHDAY".equals(var))
						applicant.setBirthday(Common.dateOf(value));
					else if ("GENDER".equals(var))
						applicant.setGenderCode("M".equals(value) ? Customer.GENDER_MALE : "F".equals(value) ? Customer.GENDER_FEMALE : Customer.GENDER_UNKNOW);
					else
						applicant.set(var, value);
				}
				else if ("primary".equals(v))
				{
					Commodity c = plan.getCommodity(0);
					c.setValue(var, value);
				}
				else if (v != null)
				{
					Commodity c = plan.getCommodityByProductId(v);
					if (c != null)
						c.setValue(var, value);
				}
			}
		}

		plan.clearCache();
	}
}
