package lerrain.service.proposal;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lerrain.service.common.ServiceMgr;
import lerrain.tool.Common;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class ProposalController
{
	@Autowired ServiceMgr serviceMgr;

	@Autowired ProposalService ps;
	@Autowired ProposalTool proposalTool;

	@RequestMapping("/create.json")
	@ResponseBody
	@CrossOrigin
	public JSONObject create(@RequestBody JSONObject p)
	{
		Long owner = p.getLong("owner");
		Long platformId = p.getLong("platformId");
		if (owner == null || platformId == null)
			throw new RuntimeException("owner or platform is null - " + p);

		JSONObject applicant = p.getJSONObject("applicant");
		Proposal proposal = ps.newProposal(applicant, owner, platformId);

		fill(proposal, p);

		JSONObject res = new JSONObject();
		res.put("result", "success");
		res.put("content", proposalTool.jsonOf(proposal));

		return res;
	}

	@RequestMapping("/modify.json")
	@ResponseBody
	@CrossOrigin
	public JSONObject modify(@RequestBody JSONObject p)
	{
		Proposal proposal = getProposal(p);

		JSONObject applicant = p.getJSONObject("applicant");
		if (applicant != null)
			proposal.setApplicant(applicant);

		fill(proposal, p);

		JSONObject res = new JSONObject();
		res.put("result", "success");
		res.put("content", proposalTool.jsonOf(proposal));

		return res;
	}

	private void fill(Proposal proposal, JSONObject p)
	{
		if (p.containsKey("name"))
			proposal.setName(p.getString("name"));
		if (p.containsKey("cover"))
			proposal.setCover(p.getString("cover"));
		if (p.containsKey("bless"))
			proposal.setBless(p.getString("bless"));
		if (p.containsKey("remark"))
			proposal.setRemark(p.getString("remark"));

		JSONObject other = p.getJSONObject("other");
		if (other != null)
			proposal.getOther().putAll(other);

		if (p.containsKey("detail"))
		{
			proposal.getPlanList().clear();
			JSONArray list = p.getJSONArray("detail");
			for (int i=0;i<list.size();i++)
				proposal.addPlan(list.getString(i));
		}

		if (p.containsKey("removePlan"))
		{
			JSONArray list = p.getJSONArray("removePlan");
			for (int i=0;i<list.size();i++)
				proposal.getPlanList().remove(list.getString(i));
		}

		if (p.containsKey("addPlan"))
		{
			JSONArray list = p.getJSONArray("addPlan");
			for (int i=0;i<list.size();i++)
				proposal.addPlan(list.getString(i));
		}
	}

	@RequestMapping("/list.json")
	@ResponseBody
	public JSONObject list(@RequestBody JSONObject p)
	{
		Long owner = p.getLong("owner");
		Long platformId = p.getLong("platformId");

		int from = Common.intOf(p.get("from"), 0);
		int num = Common.intOf(p.get("num"), 10);

		JSONObject r = new JSONObject();
		r.put("list", ps.list(null, from, num, platformId, owner));
		r.put("total", ps.count(null, platformId, owner));

		JSONObject res = new JSONObject();
		res.put("result", "success");
		res.put("content", r);

		return res;
	}

	@RequestMapping("/query.json")
	@ResponseBody
	public JSONObject query(@RequestBody JSONObject p)
	{
		Long owner = p.getLong("owner");
		Long platformId = p.getLong("platformId");

		int from = Common.intOf(p.get("from"), 0);
		int num = Common.intOf(p.get("num"), 10);

		JSONObject res = new JSONObject();
		res.put("result", "success");
		res.put("content", ps.list(null, from, num, platformId, owner));

		return res;
	}

	@RequestMapping("/supply.json")
	@ResponseBody
	public JSONObject supply(@RequestBody JSONObject p)
	{
		Proposal proposal = getProposal(p);

		String name = p.getString("name");
		proposal.setName(name);
		String cover = p.getString("cover");
		proposal.setCover(cover);
		String bless = p.getString("bless");
		proposal.setBless(bless);
		String remark = p.getString("remark");
		proposal.setRemark(remark);

		JSONObject other = p.getJSONObject("other");
		if (other != null)
			proposal.getOther().putAll(other);

		ps.saveSupply(proposal);

		JSONObject res = new JSONObject();
		res.put("result", "success");
		res.put("content", proposalTool.jsonOf(proposal));

		return res;
	}

	@RequestMapping("/save.json")
	@ResponseBody
	@CrossOrigin
	public JSONObject save(@RequestBody JSONObject p)
	{
		Proposal proposal = getProposal(p);
		boolean r = ps.saveProposal(proposal);

		JSONObject res = new JSONObject();
		if (r)
		{
			res.put("result", "success");
			res.put("content", proposalTool.jsonOf(proposal));
		}
		else
		{
			res.put("result", "fail");
			res.put("reason", "内容为空，不能保存");
		}
		return res;
	}

	@RequestMapping("/delete.json")
	@ResponseBody
	@CrossOrigin
	public JSONObject delete(@RequestBody JSONObject p)
	{
		Long proposalId = p.getLong("proposalId");

		if (proposalId == null)
			throw new RuntimeException("缺少proposalId");

		ps.delete(proposalId);

		JSONObject res = new JSONObject();
		res.put("result", "success");

		return res;
	}

	@RequestMapping("/customer.json")
	@ResponseBody
	@CrossOrigin
	public JSONObject customer(@RequestBody JSONObject p)
	{
		Proposal proposal = getProposal(p);

		JSONObject applicant = p.getJSONObject("applicant");
		JSONArray insurants = p.getJSONArray("insurants");

		JSONObject req = new JSONObject();
		if (applicant != null)
			req.put("applicant", applicant);

		if (applicant != null)
			proposal.setApplicant(applicant);

		int i = 0;
		for (String planId : proposal.getPlanList())
		{
			if (insurants != null && insurants.size() > i)
				req.put("insurant", insurants.getJSONObject(i));

			if (applicant != null || req.get("insurant") != null)
			{
				req.put("planId", planId);
				serviceMgr.req("lifeins", "plan/customer.json", req);
			}

			i++;
		}

		JSONObject res = new JSONObject();
		res.put("result", "success");
		res.put("content", proposalTool.jsonOf(proposal));

		return res;
	}
	
	@RequestMapping("/applicant.json")
	@ResponseBody
	@CrossOrigin
	public JSONObject applicant(@RequestBody JSONObject p)
	{
		Proposal proposal = getProposal(p);
		
		JSONObject applicant = p.getJSONObject("applicant");
		if (applicant != null)
		{
			JSONObject req = new JSONObject();
			req.put("applicant", applicant);
			
			proposal.setApplicant(applicant);
			for (String planId : proposal.getPlanList())
			{
				req.put("planId", planId);
				serviceMgr.req("lifeins", "plan/customer.json", req);
			}
		}
		
		JSONObject res = new JSONObject();
		res.put("result", "success");
		res.put("content", proposalTool.jsonOf(proposal));
		
		return res;
	}
	
	@RequestMapping("/insurant.json")
	@ResponseBody
	@CrossOrigin
	public JSONObject insurant(@RequestBody JSONObject p)
	{
		Proposal proposal = getProposal(p);

		JSONObject insurant = p.getJSONObject("insurant");
		if (insurant != null)
		{
			for (String planId : proposal.getPlanList())
			{
				if (planId.equals(p.getString("planId")))
				{
					JSONObject req = new JSONObject();
					req.put("planId", planId);
					req.put("insurant", insurant);

					serviceMgr.req("lifeins", "plan/customer.json", req);
					break;
				}
			}
		}
		
		JSONObject res = new JSONObject();
		res.put("result", "success");
		res.put("content", proposalTool.jsonOf(proposal));
		
		return res;
	}
	
	@RequestMapping("/view.json")
	@ResponseBody
	@CrossOrigin
	public JSONObject view(@RequestBody JSONObject p)
	{
		JSONObject res = new JSONObject();
		res.put("result", "success");
		res.put("content", proposalTool.jsonOf(getProposal(p)));
		
		return res;
	}

	@RequestMapping("/load.json")
	@ResponseBody
	@CrossOrigin
	public JSONObject load(@RequestBody JSONObject p)
	{
		Long proposalId = p.getLong("proposalId");

		if (proposalId == null)
			throw new RuntimeException("缺少proposalId");

		//dao里触发用proposal保存的plan串重新create plan，所以连计划也全部重新读了
		Proposal proposal = ps.reload(proposalId);

		JSONObject res = new JSONObject();
		res.put("result", "success");
		res.put("content", proposalTool.jsonOf(proposal));

		return res;
	}

	@RequestMapping("/fee.json")
	@ResponseBody
	@CrossOrigin
	public JSONObject fee(@RequestBody JSONObject p)
	{
		JSONObject req = new JSONObject();

		JSONArray r1 = new JSONArray();
		JSONArray r2 = new JSONArray();
		JSONArray detail = new JSONArray();

		Proposal proposal = getProposal(p);

		int size = proposal.getPlanList().size();
		JSONArray[] prm = new JSONArray[size];
		JSONArray[] fee = new JSONArray[size];

		int i = 0, l = 0;
		for (String planId : proposal.getPlanList())
		{
			req.put("planId", planId);

			JSONObject r = serviceMgr.req("lifeins", "plan/fee.json", req);

			if ("success".equals(r.get("result")))
			{
				JSONObject c = r.getJSONObject("content");
				if (c != null)
				{
					prm[i] = c.getJSONArray("premium");
					fee[i] = c.getJSONArray("commission");

					if (prm[i] != null && l < prm[i].size())
						l = prm[i].size();
					if (fee[i] != null && l < fee[i].size())
						l = prm[i].size();

					JSONObject plan = new JSONObject();
					plan.put("planId", planId);
					plan.put("premium", prm[i]);
					plan.put("commission", fee[i]);
					detail.add(plan);
				}
			}

			i++;
		}

		double[] f1 = new double[l];
		double[] f2 = new double[l];

		for (i = 0; i < size; i++)
		{
			if (prm[i] != null) for (int j = 0; j < prm[i].size(); j++)
				f1[j] += prm[i].getDoubleValue(j);
			if (fee[i] != null) for (int j = 0; j < fee[i].size(); j++)
				f2[j] += fee[i].getDoubleValue(j);
		}

		JSONObject res = new JSONObject();
		res.put("result", "success");

		JSONObject rrr = new JSONObject();
		rrr.put("premium", f1);
		rrr.put("commission", f2);
		rrr.put("detail", detail);
		res.put("content", rrr);

		return res;
	}

	private Proposal getProposal(JSONObject p)
	{
		Long proposalId = p.getLong("proposalId");
		
		if (proposalId == null)
			throw new RuntimeException("缺少proposalId");
		
		return ps.getProposal(proposalId);
	}

	@RequestMapping("/print.json")
	@ResponseBody
	public JSONObject print(@RequestBody JSONObject p)
	{
		Proposal proposal = getProposal(p);

		JSONObject pro = new JSONObject();
		JSONObject req = new JSONObject();

		JSONArray jsa = new JSONArray();
		for (String planId : proposal.getPlanList())
		{
			req.put("planId", planId);

			JSONObject r1 = serviceMgr.req("lifeins", "plan/view.json", req);
			if (!"success".equals(r1.getString("result")))
				continue;

			JSONObject plan = r1.getJSONObject("content");
			jsa.add(plan);

			if (!pro.containsKey("applicant"))
				pro.put("applicant", plan.get("applicant"));

			JSONArray prds = plan.getJSONArray("product");
			if (prds != null)
			{
				JSONObject coverage = new JSONObject();
				JSONObject table = new JSONObject();

				for (int i=0;i<prds.size();i++)
				{
					JSONObject show;
					req.put("index", i);

					req.put("type", "coverage");
					show = serviceMgr.req("lifeins", "plan/show.json", req);
					if ("success".equals(show.getString("result")))
					{
						JSONObject c2 = show.getJSONObject("content");
						coverage.putAll(c2);
					}

					req.put("type", "table");
					req.put("fold", true);
					show = serviceMgr.req("lifeins", "plan/show.json", req);
					if ("success".equals(show.getString("result")))
					{
						JSONObject c2 = show.getJSONObject("content");
						table.putAll(c2);
					}
				}

				plan.put("coverage", coverage);
				plan.put("table", table);
			}
		}

		pro.put("plan", jsa);
		if (p.containsKey("plus"))
			pro.putAll(p.getJSONObject("plus"));

		JSONObject res = new JSONObject();
		res.put("result", "success");
		res.put("content", pro);

		return res;
	}
}
