package lerrain.service.lifeins.pack;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lerrain.project.insurance.plan.Commodity;
import lerrain.project.insurance.plan.Plan;
import lerrain.project.insurance.product.Insurance;
import lerrain.project.insurance.product.rule.Rule;
import lerrain.project.insurance.product.rule.RuleUtil;
import lerrain.service.lifeins.Customer;
import lerrain.service.lifeins.LifeinsService;
import lerrain.service.lifeins.LifeinsUtil;
import lerrain.service.lifeins.QuestService;
import lerrain.service.lifeins.plan.PlanService;
import lerrain.tool.Common;
import lerrain.tool.formula.Factors;
import lerrain.tool.formula.Formula;
import lerrain.tool.formula.Function;
import lerrain.tool.script.Script;
import lerrain.tool.script.Stack;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;


@Service
public class PackService
{
	@Autowired
	PackDao packDao;

	@Autowired
	LifeinsService lifeins;

	@Autowired
	QuestService questSrv;

	@Autowired
	PlanService planSrv;
	
	Map<Object, PackIns> packs;

	Map<String, Function> functions;

	@PostConstruct
	public void initiate()
	{
		packs = packDao.loadPacks();

		functions = new HashMap<>();

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

				return new Plan(new Customer(), new Customer());
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
				return PackService.this.getRule((Plan)objects[0]);
			}
		});

		functions.put("getPremium", new Function()
		{
			@Override
			public Object run(Object[] objects, Factors factors)
			{
				return PackService.this.getPremium((Plan) objects[0]);
			}
		});

		functions.put("getChart", new Function()
		{
			@Override
			public Object run(Object[] objects, Factors factors)
			{
				return LifeinsUtil.formatChart((Commodity) objects[0]);
			}
		});

		functions.put("getTable", new Function()
		{
			@Override
			public Object run(Object[] objects, Factors factors)
			{
				return LifeinsUtil.formatTable((Commodity) objects[0], Common.boolOf(objects[1], false));
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
		vals.putAll(functions);

		if (packIns.getInputForm() != null) for (InputField field : packIns.getInputForm())
		{
			String name = field.getName();
			Object value = vals.get(name);

			if (value == null)
				continue;

			vals.put(name, PackUtil.translate(field.getType(), value));
		}

		Stack stack = new Stack(vals);

		if (packIns.getPlanId() != null)
			stack.set("plan", planSrv.getPlan(packIns.getPlanId()));

		if (packIns.getPretreat() != null)
			packIns.getPretreat().run(stack);

		Plan plan = (Plan)stack.get("plan");
		stack.set("PLAN", plan == null ? null : plan.getFactors());

		return stack;
	}

	public Object perform(Formula opt, PackIns packIns, Map<String, Object> vals)
	{
		Stack stack = factorsOf(packIns, vals);
		Plan plan = (Plan)stack.get("plan");

		if (plan == null)
		{
			return opt.run(stack);
		}
		else synchronized (plan)
		{
			reset(plan, packIns, stack);
			return opt.run(stack);
		}
	}

	public Object perform(String opt, PackIns packIns, Map<String, Object> vals)
	{
		if (packIns.getPerform() == null)
			return null;

		Stack stack = factorsOf(packIns, vals);
		stack.set("opt", opt);

		Plan plan = (Plan)stack.get("plan");

		if (plan == null)
		{
			return packIns.getPerform().run(stack);
		}
		else synchronized (plan)
		{
			reset(plan, packIns, stack);
			return packIns.getPerform().run(stack);
		}
	}

	public Map<String, Object> detail(PackIns packIns, Map<String, Object> vals)
	{
		Stack stack = factorsOf(packIns, vals);
		Plan plan = (Plan)stack.get("plan");

		if (plan == null)
		{
			return null;
		}
		else synchronized (plan)
		{
			reset(plan, packIns, stack);

			JSONObject json = LifeinsUtil.jsonOf(plan);
			json.put("name", packIns.getName());
			return json;
		}
	}

	public Map<String, Object> quest(PackIns packIns, Map<String, Object> vals)
	{
		Stack stack = factorsOf(packIns, vals);
		Plan plan = (Plan)stack.get("plan");

		Map<String, Object> r = new HashMap<>();
		synchronized (plan)
		{
			reset(plan, packIns, stack);

			r.put("applicant", questSrv.getQuests(plan, 1));
			r.put("insurant", questSrv.getQuests(plan, 2));
		}
		return r;
	}

	public Map<String, Object> fee(PackIns packIns, Map<String, Object> vals)
	{
		Stack stack = factorsOf(packIns, vals);
		Plan plan = (Plan) stack.get("plan");

		synchronized (plan)
		{
			reset(plan, packIns, stack);
			return LifeinsUtil.feeOf(plan);
		}
	}

	public Map<String, Object> premium(PackIns packIns, Map<String, Object> vals)
	{
		Map<String, Object> r = new HashMap<>();

		List<String> rules = new ArrayList<>();
		double total = 0;

		Stack stack = factorsOf(packIns, vals);
		Plan plan = (Plan)stack.get("plan");

		synchronized (plan)
		{
			reset(plan, packIns, stack);

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

				try
				{
					String code = c.getProduct().getCode();
					double premium = c.getPremium();

					r.put(code, premium);
					total += c.getPremium();
				}
				catch (Exception e)
				{
					if (rs == null || rs.isEmpty())
						rules.add(c.getProduct().getAbbrName() + "：保费计算错误");
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
		}

		r.put("premium", total); //兼容
		r.put("total", total);
		r.put("rules", rules);

		return r;
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

	private void reset(Plan plan, PackIns packIns, Stack stack)
	{
		Date date = Common.dateOf(stack.get("EFFECTIVE_DATE"));
		if (date != null)
			plan.setInsureTime(date);

		Customer applicant = (Customer)plan.getApplicant();
		applicant.setAge(30);

		boolean forSelf = "self".equals(stack.get("RELATION"));
		Customer insurant = forSelf ? applicant : new Customer();
		insurant.set("RELATIVE", stack.get("RELATION"));
		plan.setInsurant(insurant);

		for (InputField field : packIns.getInputForm())
		{
			Object value = stack.get(field.getName());
			String var = field.getVar();

			String[] scope = field.getScope();
			if (scope != null) for (String v : scope)
			{
//				System.out.println(v + "/" + var + " = " + value);

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

//			for (Map.Entry<String, Object> entry : vals.entrySet())
//			{
//				String key = entry.getKey();
//				Object val = entry.getValue();
//
//				if (keys != null && keys.containsKey(key))
//				{
//					String factorName = keysMap.get(key);
//					List refer = (List)keys.get(key);
//
//					for (Object m : refer)
//					{
//						Commodity c = plan.getCommodity(Common.intOf(m, -1));
//						if (factorName.startsWith("input"))
//							c.setInput(factorName.substring(6), val.toString());
//						else
//							c.setValue(factorName, val);
//					}
//				}
//				else if ("age".equals(key))
//					insurant.setAge(Common.intOf(val, 0));
//				else if ("gender".equals(key))
//					insurant.setGenderCode("M".equals(val) || "1".equals(val.toString()) ? Customer.GENDER_MALE : Customer.GENDER_FEMALE);
//				else if ("birthday".equals(key))
//					insurant.setBirthday(Common.dateOf(val));
//				else
//				{
//					String factorName = keysMap.get(key);
//					if (factorName != null) for (int i=0;i<plan.size();i++)
//					{
//						Commodity c = plan.getCommodity(i);
//						if (factorName.startsWith("input"))
//						{
//							List optionList = c.getProduct().getOptionList(factorName.substring(6));
//							if (optionList != null && optionList.size() > 1)
//								c.setInput(factorName.substring(6), val.toString());
//						}
//						else
//						{
//							int inputMode = c.getProduct().getInputMode();
//
//							if ("amount".equals(key) && (inputMode & Purchase.AMOUNT) > 0)
//								continue;
//							if ("premium".equals(key) && (inputMode & Purchase.PREMIUM) > 0)
//								continue;
//							if ("quantity".equals(key) && (inputMode & Purchase.QUANTITY) > 0)
//								continue;
//
//							c.setValue(factorName, val);
//						}
//					}
//				}
//			}

		plan.clearCache();
	}
}
