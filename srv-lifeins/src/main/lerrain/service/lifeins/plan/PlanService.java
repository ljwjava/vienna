package lerrain.service.lifeins.plan;

import lerrain.project.insurance.plan.Plan;
import lerrain.project.insurance.product.Company;
import lerrain.project.insurance.product.Insurance;
import lerrain.tool.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Service
public class PlanService
{
	@Autowired PlanDao pd;
	@Autowired PlanDao2 pd2;

	Cache cache = new Cache();

	@PostConstruct
	public void reset()
	{
		List<Plan> list = pd2.loadAll();
		if (list != null)
			for (Plan plan : list)
				if (plan != null)
					cache.put(plan.getId(), plan);
	}

	public Plan getPlan(String planId)
	{
		if (planId == null)
			return null;

		Plan plan = cache.get(planId);
		
		if (plan == null)
		{
			plan = pd.load(planId);
			cache.put(plan.getId(), plan);
		}
		
		return plan;
	}
	
	public void savePlan(Plan p)
	{
		pd.save(p);
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
}
