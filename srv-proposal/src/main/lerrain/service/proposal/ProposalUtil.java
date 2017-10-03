package lerrain.service.proposal;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class ProposalUtil
{
	public static JSONObject jsonOf(Proposal proposal)
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
}
