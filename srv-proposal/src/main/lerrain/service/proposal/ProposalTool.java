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
		r.put("bless", proposal.getBless());
		r.put("insureTime", proposal.getInsureTime());
		r.put("remark", proposal.getRemark());
		r.put("other", proposal.getOther());
		r.put("owner", proposal.getOwner());
		
		JSONArray list = new JSONArray();
		for (String planId : proposal.getPlanList())
			list.add(planId);
		
		r.put("detail", list);

		return r;
	}
}
