package lerrain.service.proposal;

import java.math.BigDecimal;
import java.util.*;

public class Proposal
{
	Long id;
	String name;
	String remark;
	String cover;
	String bless;

	Long owner;
	Long platformId;

	boolean favourite;

	String[] tag;

	Date insureTime;
	Date updateTime;

	BigDecimal premium;

	Map<String, Object> applicant;
	
	List<String> planList = new ArrayList<>();

	Map<String, Object> other = new HashMap<>();

	public boolean isFavourite()
	{
		return favourite;
	}

	public void setFavourite(boolean favourite)
	{
		this.favourite = favourite;
	}

	public String getBless()
	{
		return bless;
	}

	public void setBless(String bless)
	{
		this.bless = bless;
	}

	public void setOther(Map<String, Object> other)
	{
		this.other = other;
	}

	public void setPlanList(List<String> planList)
	{
		this.planList = planList;
	}

	public Date getInsureTime()
	{
		return insureTime;
	}

	public Map<String, Object> getApplicant()
	{
		return applicant;
	}

	public void setApplicant(Map<String, Object> applicant)
	{
		this.applicant = applicant;
	}

	public void setInsureTime(Date insureTime)
	{
		this.insureTime = insureTime;
	}

	public String getCover()
	{
		return cover;
	}

	public void setCover(String cover)
	{
		this.cover = cover;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getRemark()
	{
		return remark;
	}

	public void setRemark(String remark)
	{
		this.remark = remark;
	}

	public String[] getTag()
	{
		return tag;
	}

	public void setTag(String[] tag)
	{
		this.tag = tag;
	}

	public BigDecimal getPremium()
	{
		return premium;
	}

	public void setPremium(BigDecimal premium)
	{
		this.premium = premium;
	}

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public Date getUpdateTime()
	{
		return updateTime;
	}

	public void setUpdateTime(Date updateTime)
	{
		this.updateTime = updateTime;
	}

	public Long getOwner()
	{
		return owner;
	}

	public void setOwner(Long owner)
	{
		this.owner = owner;
	}

	public void addPlan(String planId)
	{
		if (planList.indexOf(planId) < 0)
			planList.add(planId);
	}

	public Long getPlatformId()
	{
		return platformId;
	}

	public void setPlatformId(Long platformId)
	{
		this.platformId = platformId;
	}

	public Map<String, Object> getOther()
	{
		return other;
	}

	public List<String> getPlanList()
	{
		return planList;
	}
}
