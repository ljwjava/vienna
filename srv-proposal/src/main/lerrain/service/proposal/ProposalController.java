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

	@RequestMapping("/cover.json")
	@ResponseBody
	@CrossOrigin
	public JSONObject cover(@RequestBody JSONObject p)
	{
		JSONObject res = new JSONObject();
		res.put("result", "success");
		res.put("content", ps.getCovers());

		return res;
	}

	@RequestMapping("/list.json")
	@ResponseBody
	@CrossOrigin
	public JSONObject list(@RequestBody JSONObject p)
	{
		String owner = p.getString("owner");
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

	@RequestMapping("/copy.json")
	@ResponseBody
	@CrossOrigin
	public JSONObject copy(@RequestBody JSONObject p)
	{
		Proposal proposal = getProposal(p);
		Proposal newPro = ps.copy(proposal);

		JSONObject req = new JSONObject();
		for (String planId : proposal.getPlanList())
		{
			req.put("planId", planId);
			JSONObject r = serviceMgr.req("lifeins", "export_keys.json", req);
			r = serviceMgr.req("lifeins", "create.json", r.getJSONObject("content"));

			newPro.addPlan(r.getJSONObject("content").getString("planId"));
		}

		JSONObject res = new JSONObject();
		res.put("result", "success");
		res.put("content", proposalTool.jsonOf(proposal));

		return res;
	}

	@RequestMapping("/create.json")
	@ResponseBody
	@CrossOrigin
	public JSONObject create(@RequestBody JSONObject p)
	{
		JSONObject applicant = p.getJSONObject("applicant");

		Proposal proposal = ps.newProposal(applicant);
		proposal.setOwner(p.getString("owner"));
		proposal.setPlatformId(p.getLong("platformId"));

		JSONArray insurants = p.getJSONArray("insurants");
		if (insurants != null) for (int i = 0; i < insurants.size(); i++)
		{
			String planId = createPlan(proposal.getApplicant(), insurants.getJSONObject(i));
			if (planId != null)
				proposal.addPlan(planId);
		}
		else if (p.containsKey("insurant"))
		{
			String planId = createPlan(proposal.getApplicant(), p.getJSONObject("insurant"));
			if (planId != null)
				proposal.addPlan(planId);
		}

		JSONObject res = new JSONObject();
		res.put("result", "success");
		res.put("content", proposalTool.jsonOf(proposal));
		
		return res;
	}

	@RequestMapping("/favourite.json")
	@ResponseBody
	@CrossOrigin
	public JSONObject favourite(@RequestBody JSONObject p)
	{
		String proposalId = p.getString("proposalId");

		if (Common.isEmpty(proposalId))
			throw new RuntimeException("缺少proposalId");

		boolean b = Common.boolOf(p.get("favourite"), false);
		ps.setFavourite(proposalId, b);

		JSONObject res = new JSONObject();
		res.put("result", "success");

		return res;
	}

	@RequestMapping("/supply.json")
	@ResponseBody
	@CrossOrigin
	public JSONObject supply(@RequestBody JSONObject p)
	{
		Proposal proposal = getProposal(p);

		String name = p.getString("name");
		proposal.setName(name);
		String cover = p.getString("cover");
		proposal.setCover(cover);
		String bless = p.getString("bless");
		proposal.setBless(bless);

		JSONObject other = p.getJSONObject("other");
		if (other != null)
			proposal.getOther().putAll(other);

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
			res.put("reason", "不能保存空建议书");
		}
		return res;
	}

	@RequestMapping("/delete.json")
	@ResponseBody
	@CrossOrigin
	public JSONObject delete(@RequestBody JSONObject p)
	{
		String proposalId = p.getString("proposalId");

		if (Common.isEmpty(proposalId))
			throw new RuntimeException("缺少proposalId");

		ps.delete(p.getString("proposalId"));

		JSONObject res = new JSONObject();
		res.put("result", "success");

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
				if (planId.equals(p.get("planId")))
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
	
	@RequestMapping("/create_plan.json")
	@ResponseBody
	@CrossOrigin
	public JSONObject createPlan(@RequestBody JSONObject p)
	{
		Proposal proposal = getProposal(p);
		
		JSONArray insurants = p.getJSONArray("insurants");
		for (int i = 0; i < insurants.size(); i++)
		{
			String planId = createPlan(proposal.getApplicant(), insurants.getJSONObject(i));
			if (planId != null)
				proposal.addPlan(planId);
		}
		
		JSONObject res = new JSONObject();
		res.put("result", "success");
		res.put("content", proposalTool.jsonOf(proposal));
		
		return res;
	}

	private String createPlan(Object applicant, Object insurant)
	{
		JSONObject req = new JSONObject();
		req.put("applicant", applicant);
		req.put("insurant", insurant);

		JSONObject rsp = serviceMgr.req("lifeins", "plan/create.json", req);
		if ("success".equals(rsp.get("result")))
			return rsp.getJSONObject("content").getString("planId");

		return null;
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
		String proposalId = p.getString("proposalId");

		if (Common.isEmpty(proposalId))
			throw new RuntimeException("缺少proposalId");

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
			JSONObject r = serviceMgr.req("lifeins", "fee.json", req);

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

	@RequestMapping("/delete_plan.json")
	@ResponseBody
	@CrossOrigin
	public JSONObject deletePlan(@RequestBody JSONObject p)
	{
		Proposal proposal = getProposal(p);
		proposal.getPlanList().remove(p.get("planId"));

		JSONObject res = new JSONObject();
		res.put("result", "success");
		res.put("content", proposalTool.jsonOf(proposal));

		return res;
	}

	@RequestMapping("/list_clauses.json")
	@ResponseBody
	@CrossOrigin
	public JSONObject listClauses(@RequestBody JSONObject p)
	{
		JSONArray prds = new JSONArray();

		for (Product prd : ps.getClauses(p.getLong("platformId")))
		{
			if (prd.getType() != 2)
			{
				JSONObject item = new JSONObject();
				item.put("id", prd.getId());
				item.put("name", prd.getName());
				item.put("tag", prd.getTag());
				item.put("logo", prd.getLogo());
				item.put("remark", prd.getRemark());

				prds.add(item);
			}
		}

		JSONObject res = new JSONObject();
		res.put("result", "success");
		res.put("content", prds);

		return res;
	}
	
	private Proposal getProposal(JSONObject p)
	{
		String proposalId = p.getString("proposalId");
		
		if (Common.isEmpty(proposalId))
			throw new RuntimeException("缺少proposalId");
		
		return ps.getProposal(proposalId);
	}

	@RequestMapping("/overview.json")
	@ResponseBody
	@CrossOrigin
	public JSONObject overview(@RequestBody JSONObject p)
	{
		JSONObject r = new JSONObject();

		Proposal proposal = getProposal(p);
		r.put("bless", proposal.getOther().get("bless"));

		JSONObject req = new JSONObject();
		for (String planId : proposal.getPlanList())
		{
			req.put("planId", planId);
			req.put("style", "fgraph,csv");

			JSONObject pl = new JSONObject();

			JSONObject r1 = serviceMgr.req("lifeins", "plan/format.json", req);
			pl.putAll(r1.getJSONObject("content"));

			r.put(planId, pl);
		}

		JSONObject res = new JSONObject();
		res.put("result", "success");
		res.put("content", r);

		return res;
	}

	@RequestMapping("/apply.json")
	@ResponseBody
	@CrossOrigin
	public JSONObject apply(@RequestBody JSONObject p)
	{
		JSONObject detail = proposalTool.apply(getProposal(p));
		detail.put("platformId", p.getLong("platformId"));

		return serviceMgr.req("sale", "do/proposal_apply.json", detail);
	}

	@RequestMapping("/print.json")
	@ResponseBody
	@CrossOrigin
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

	@RequestMapping("/plan/{path}.json")
	@ResponseBody
	@CrossOrigin
	public JSONObject redirect(@PathVariable String path, @RequestBody JSONObject req)
	{
		return serviceMgr.req("lifeins", "plan/" + path + ".json", req);
	}
}
