package lerrain.service.proposal;

import lerrain.service.common.ServiceTools;
import lerrain.tool.Cache;
import lerrain.tool.Common;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;

import java.util.*;

@Service
public class ProposalService
{
	@Autowired
	ProposalDao proposalDao;

	@Autowired
	ServiceTools tools;

	List<Object> covers = new ArrayList<>();

	Cache cache = new Cache();

	public List<Object> getCovers()
	{
		synchronized (covers)
		{
			if (covers.isEmpty())
			{
				for (String[] s : proposalDao.getCoverList())
				{
					JSONObject c = new JSONObject();
					c.put("coverId", s[0]);
					c.put("url", s[1]);
					c.put("preview", s[2]);
					covers.add(c);
				}
			}
		}

		return covers;
	}

	public Proposal copy(Proposal old)
	{
		Map newApplicant = null;
		Map oldApplicant = old.getApplicant();
		if (oldApplicant != null)
		{
			newApplicant = new HashMap();
			newApplicant.putAll(oldApplicant);
		}

		Proposal np = newProposal(newApplicant, old.getOwner(), old.getPlatformId());
		np.setId(tools.nextId("proposal"));
		np.setName(old.getName());
		np.setRemark(old.getRemark());
		np.setCover(old.getCover());
		np.setFavourite(false);
		np.setTag(old.getTag());
		np.setOwner(old.getOwner());
		np.setInsureTime(new Date());
		np.setUpdateTime(new Date());
		np.getOther().putAll(old.getOther());

		cache.put(np.getId(), np);

		return np;
	}

	public Proposal newProposal(Map<String, Object> applicant, Long userId, Long platformId)
	{
		if (applicant == null)
			applicant = standardCustomer();

		Proposal proposal = new Proposal();
		proposal.setApplicant(applicant);
		proposal.setId(tools.nextId("proposal"));
		proposal.setOwner(userId);
		proposal.setPlatformId(platformId);

		cache.put(proposal.getId(), proposal);

		return proposal;
	}

	public Proposal reload(Long id)
	{
		Proposal proposal = proposalDao.load(id);
		cache.put(id, proposal);

		return proposal;
	}
	
	public Proposal getProposal(Long id)
	{
		Proposal proposal = cache.get(id);

		if (proposal == null)
		{
			proposal = proposalDao.load(id);
			cache.put(id, proposal);
		}

		return proposal;
	}

	public void setFavourite(Long proposalId, boolean fav)
	{
		this.getProposal(proposalId).setFavourite(fav);
		proposalDao.setFavourite(proposalId, fav);
	}

	private Map<String, Object> standardCustomer()
	{
		JSONObject applicant = new JSONObject();
		applicant.put("id", tools.nextId("customer"));
		applicant.put("birthday", Common.dateOf("1990-01-01"));
		applicant.put("gender", "M");

		return applicant;
	}

	public List<Proposal> list(String search, int from, int number, Long platformId, Long owner)
	{
		return proposalDao.list(search, from, number, platformId, owner);
	}

	public int count(String search, Long platformId, Long owner)
	{
		return proposalDao.count(search, platformId, owner);
	}

	public void delete(Long proposalId)
	{
		proposalDao.delete(proposalId);
	}

	public boolean saveProposal(Proposal p)
	{
		return proposalDao.save(p);
	}

	public void saveSupply(Proposal p)
	{
		proposalDao.updateSupply(p);
	}

	public List<Object[]> listBless(Long owner, Long platformId)
	{
		return proposalDao.listBless(owner, platformId);
	}

	public void saveBless(Long blessId, String text, Long owner, Long platformId)
	{
		proposalDao.saveBless(blessId, text, owner, platformId);
	}

	public void deleteBless(Long blessId)
	{
		proposalDao.deleteBless(blessId);
	}

	public boolean addProposal(Proposal p)
	{
		return proposalDao.saveProposal(p);
	}


	public void  createAgent(Long accountId,String name,String mobile,String detail){
		proposalDao.createAgent(accountId,name,mobile,detail);
	}

	public JSONObject  queryAgent(Long accountId ){
		return proposalDao.queryAgent(accountId );
	}

}
