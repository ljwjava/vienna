package lerrain.service.proposal;

public class ProposalPlan
{
	String planId;
	
//	JSONObject insurant;
	
//	public ProposalPlan(String planId, Map<String, Object> insurant)
//	{
//		this.planId = planId;
//		this.insurant = insurant;
//	}

	public String getPlanId()
	{
		return planId;
	}

	public void setPlanId(String planId)
	{
		this.planId = planId;
	}

//	public JSONObject getInsurant()
//	{
//		return insurant;
//	}
//
//	public void setInsurant(JSONObject insurant)
//	{
//		this.insurant = insurant;
//	}
	
	@Override
	public boolean equals(Object v)
	{
		if (v instanceof ProposalPlan)
			return ((ProposalPlan)v).planId.equals(planId);
		
		return false;
	}
}
