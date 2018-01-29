package lerrain.service.proposal;

import java.rmi.activation.ActivationGroup_Stub;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSON;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lerrain.service.common.ServiceMgr;
import lerrain.service.common.ServiceTools;
import lerrain.tool.Common;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class ProposalDao
{
	@Autowired
	ServiceMgr serviceMgr;

	@Autowired
	ServiceTools tools;

	@Autowired
	JdbcTemplate jdbc;
	
	public List<String[]> getCoverList()
	{
		return jdbc.query("select * from t_proposal_cover order by cover_id", new RowMapper<String[]>()
		{
			@Override
			public String[] mapRow(ResultSet m, int arg1) throws SQLException 
			{
				String coverId = m.getString("cover_id");
				String coverUrl = m.getString("cover_url");
				String previewUrl = m.getString("preview_url");

				return new String[] {coverId, coverUrl, previewUrl};
			}
		});
	}

	public int count(String search, final Long platformId, final Long owner)
	{
		StringBuffer sql = new StringBuffer("select count(*) from t_proposal where platform_id = ? and creator = ? and valid is null");
		if (search != null && !"".equals(search))
			sql.append(" and proposal_name like '%" + search + "%' ");

		return jdbc.queryForObject(sql.toString(), Integer.class, platformId, owner);
	}
	
	public List<Proposal> list(String search, int from, int number, final Long platformId, final Long owner)
	{
		StringBuffer sql = new StringBuffer("select * from t_proposal where platform_id = ? and creator = ? and valid is null");
		if (search != null && !"".equals(search))
			sql.append(" and proposal_name like '%" + search + "%' ");
		sql.append(" order by favourite desc, update_time desc");
		sql.append(" limit " + from + ", " + number);
		
		return jdbc.query(sql.toString(), new Object[] {platformId, owner}, new RowMapper<Proposal>()
		{
			@Override
			public Proposal mapRow(ResultSet m, int arg1) throws SQLException 
			{
				Proposal p = new Proposal();
				p.setId(m.getLong("proposal_id"));
				p.setName(m.getString("proposal_name"));
				p.setRemark(m.getString("remark"));
				p.setCover(m.getString("cover"));
				p.setBless(m.getString("bless"));
				p.setPremium(m.getBigDecimal("premium"));
				p.setInsureTime(m.getDate("insure_time"));
				p.setPlatformId(Common.toLong(m.getString("platform_id")));
				p.setFavourite("Y".equalsIgnoreCase(m.getString("favourite")));
				p.setOwner(owner);
				p.setUpdateTime(m.getTimestamp("update_time"));

				String tag = m.getString("tag");
				if (tag != null)
					p.setTag(tag.split(","));

				return p;
			}
		});
	}

	public void setFavourite(Long proposalId, boolean fav)
	{
		String sql = "update t_proposal set favourite = ? where proposal_id = ?";
		jdbc.update(sql, fav ? "Y" : null, proposalId);
	}
	
	public boolean save(Proposal c)
	{
		if (c == null || c.getApplicant() == null || c.getPlanList() == null || c.getPlanList().isEmpty())
			return false;
		
		Date now = new Date();
		
		Long proposalId = c.getId();
		String applicant = JSON.toJSONString(c.getApplicant());
		String other = c.getOther() == null ? null : JSON.toJSONString(c.getOther());

		boolean isNew = true;
		
		if (proposalId != null)
		{
			String sql1 = "select count(*) from t_proposal where proposal_id = ?";
			int num = jdbc.queryForObject(sql1, new Object[] {proposalId}, Integer.class);
			isNew = num == 0;
		}
		else
		{
			proposalId = tools.nextId("proposal");
			c.setId(proposalId);
		}

		boolean pass = false;

		double total = 0;
		JSONArray plans = new JSONArray();

		JSONObject req = new JSONObject();
		for (String planId : c.getPlanList())
		{
			req.put("planId", planId);

			JSONObject res = serviceMgr.req("lifeins", "plan/export_keys.json", req);
			JSONObject plan = res.getJSONObject("content");

			total += Common.doubleOf(plan.get("premium"), 0);
			plans.add(plan);

			if (plan.containsKey("product") && !plan.getJSONArray("product").isEmpty())
				pass = true;
		}

		if (!pass)
			return false;

		String tag = plans.size() > 1 ? "multi" : "single";

		if (isNew)
		{
			String sql = "insert into t_proposal(proposal_id, proposal_name, bless, other, remark, applicant, insure_time, cover, plans, tag, premium, platform_id, creator, create_time, updater, update_time) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			jdbc.update(sql, proposalId, c.getName(), c.getBless(), other, c.getRemark(), applicant, c.getInsureTime(), c.getCover(), plans.toString(), tag, total, c.getPlatformId(), c.getOwner(), now, c.getOwner(), now);
		}
		else
		{
			String sql = "update t_proposal set proposal_name = ?, bless = ?, other = ?, remark = ?, applicant = ?, insure_time = ?, cover = ?, plans = ?, tag = ?, premium = ?, updater = ?, update_time = ? where proposal_id = ?";
			jdbc.update(sql, c.getName(), c.getBless(), other, c.getRemark(), applicant, c.getInsureTime(), c.getCover(), plans.toString(), tag, total, c.getOwner(), now, proposalId);
		}

		return true;
	}

	public void updateSupply(Proposal c)
	{
		String other = c.getOther() == null ? null : JSON.toJSONString(c.getOther());
		String tag = c.getPlanList() != null && c.getPlanList().size() > 1 ? "multi" : "single";

		String sql = "update t_proposal set proposal_name = ?, bless = ?, other = ?, remark = ?, insure_time = ?, cover = ?, tag = ?, updater = ?, update_time = ? where proposal_id = ?";
		jdbc.update(sql, c.getName(), c.getBless(), other, c.getRemark(), c.getInsureTime(), c.getCover(), tag, c.getOwner(), new Date(), c.getId());
	}
	
	public boolean delete(Long proposalId)
	{
		String sql = "update t_proposal set valid = 'N', update_time = ? where proposal_id = ?";
		jdbc.update(sql, new Date(), proposalId);
		
		return true;
	}
	
	public Proposal load(Long proposalId)
	{
		return jdbc.queryForObject("select a.* from t_proposal a where a.proposal_id = ?", new Object[] {proposalId}, new RowMapper<Proposal>()
		{
			@Override
			public Proposal mapRow(ResultSet rs, int arg1) throws SQLException 
			{
				Proposal p = new Proposal();
				p.setId(rs.getLong("proposal_id"));
				p.setApplicant(JSON.parseObject(rs.getString("applicant")));
				p.setName(rs.getString("proposal_name"));
				p.setRemark(rs.getString("remark"));
				p.setBless(rs.getString("bless"));
				p.setCover(rs.getString("cover"));
				p.setPremium(rs.getBigDecimal("premium"));
				p.setPlatformId(Common.toLong(rs.getString("platform_id")));
				p.setFavourite("Y".equalsIgnoreCase(rs.getString("favourite")));
				p.setOwner(rs.getLong("creator"));
				p.setInsureTime(rs.getDate("insure_time"));
				p.setUpdateTime(rs.getDate("update_time"));

				String tag = rs.getString("tag");
				if (tag != null)
					p.setTag(tag.split(","));

				String otherStr = rs.getString("other");
				if (!Common.isEmpty(otherStr))
					p.setOther(JSON.parseObject(otherStr));

				JSONArray list = JSON.parseArray(rs.getString("plans"));
				for (int i = 0; i < list.size(); i++)
				{
					JSONObject plan = list.getJSONObject(i);
					serviceMgr.req("lifeins", "plan/create.json", plan);

					p.addPlan(plan.getString("planId"));
				}

				return p;
			}
		});
	}

	public List<Object[]> listBless(Long owner, Long platformId)
	{
		return jdbc.query("select a.* from t_proposal_bless a where a.owner = ? and a.valid is null and a.platform_id = ? order by a.update_time desc", new Object[]{owner, platformId}, new RowMapper<Object[]>()
		{
			@Override
			public Object[] mapRow(ResultSet rs, int arg1) throws SQLException
			{
				return new Object[]{rs.getLong("bless_id"), rs.getString("text")};
			}
		});
	}

	public void saveBless(Long blessId, String text, Long owner, Long platformId)
	{
		if (blessId == null)
		{
			String sql = "insert into t_proposal_bless(text, owner, platform_id, update_time) values(?, ?, ?, ?)";
			jdbc.update(sql, text, owner, platformId, new Date());
		}
		else
		{
			String sql = "update t_proposal_bless set text = ?, update_time = ? where bless_id = ?";
			jdbc.update(sql, text, new Date(), blessId);
		}
	}

	public void deleteBless(Long blessId)
	{
		String sql = "update t_proposal_bless set valid = 'N', update_time = ? where bless_id = ?";
		jdbc.update(sql, new Date(), blessId);
	}
}
