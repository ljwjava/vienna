package lerrain.service.proposal;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lerrain.service.common.ServiceMgr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProposalTool
{
	@Autowired
	ServiceMgr serviceMgr;

	public JSONObject jsonOf(Proposal proposal)
	{
		JSONObject r = new JSONObject();

		r.put("proposalId", proposal.getId());
		r.put("name", proposal.getName());
		r.put("applicant", proposal.getApplicant());
		r.put("cover", proposal.getCover());
		r.put("insureTime", proposal.getInsureTime());
		r.put("remark", proposal.getRemark());
		r.put("other", proposal.getOther());
		
		JSONArray list = new JSONArray();
		for (String planId : proposal.getPlanList())
			list.add(planId);
		
		r.put("detail", list);

		return r;
	}

	public JSONObject apply(Proposal proposal)
	{
		JSONObject r = new JSONObject();

		r.put("applicant", proposal.getApplicant());
		r.put("proposalId", proposal.getId());

		double premium = 0;

		JSONArray plans = new JSONArray();
		JSONObject req = new JSONObject();
		for (String planId : proposal.getPlanList())
		{
			req.put("planId", planId);

			JSONObject plan = serviceMgr.req("lifeins", "plan/view.json", req).getJSONObject("content");
			premium += plan.getDoubleValue("premium");
			plans.add(plan);
		}
		r.put("plan", plans);
		r.put("premium", premium);

		return r;
	}
}
