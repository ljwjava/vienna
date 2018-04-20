package lerrain.service.lifeins.format;

import lerrain.project.insurance.plan.Commodity;
import lerrain.project.insurance.plan.Plan;
import lerrain.project.insurance.plan.filter.FilterPlan;
import lerrain.tool.Common;
import lerrain.tool.formula.Formula;
import lerrain.tool.formula.Value;
import lerrain.tool.script.Script;
import lerrain.tool.script.Stack;

import java.util.*;


public class BenefitFilter implements FilterPlan
{
	@Override
	public Object filtrate(Plan plan, String attachmentName)
	{
		List res = new ArrayList();

		String[] axis = null;
		int age = Common.getAge(plan.getInsurant().getBirthday());

		List<Map> tabs = (List) plan.primaryCommodity().getCompany().getAttachment(attachmentName);
		for (Map m : tabs)
		{
			int mx = 0, my = 0;
			double[][] vv = new double[10][200];
			String[] tit = new String[10];

			for (int i = 0; i < plan.size(); i++)
			{
				Commodity c = plan.getCommodity(i);

				String var = (String) m.get("var");
				if (var == null)
					var = "I";
				Stack s = new Stack(c.getFactors());

				for (int j = 0; j < c.getInsure().getPeriodYear(); j++)
				{
					int k = 0;
					for (Map x : (List<Map>) m.get("list"))
					{
						tit[k] = (String)x.get("name");
						Formula f = (Formula) x.get("formula");
						s.declare(var, j);

						double xx = 0;

						try
						{
							xx = Value.doubleOf(f, s);
						}
						catch (Exception e)
						{
						}

						vv[k++][j] += xx;
					}

					my = k;
				}

				mx = Math.max(mx, c.getInsure().getPeriodYear());
			}

			double[][] dst = new double[my][mx];
			for (int i = 0; i < my; i++)
				dst[i] = Arrays.copyOf(vv[i], mx);

			if (axis == null)
			{
				axis = new String[mx];
				for (int i = 0; i < mx; i++)
					axis[i] = i + age + "岁";
			}

			Map r = new HashMap();
			r.put("title", Arrays.copyOf(tit, my));
			r.put("name", m.get("name"));
			r.put("value", dst);
			r.put("axis", axis);

			res.add(r);
		}

		return res;
	}
}
