package lerrain.service.org;

import lerrain.service.common.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class OrgService
{
	@Autowired
	MemberDao memberDao;

	@Autowired
	OrgDao orgDao;

	@Autowired
	CompanyDao companyDao;

	Map<Long, Member> m1 = new HashMap<>();
	Map<Long, Org> m2 = new HashMap<>();
	Map<Long, Company> m3 = new HashMap<>();

	public Member getMember(Long memberId)
	{
		synchronized (m1)
		{
			if (m1.containsKey(memberId))
				return m1.get(memberId);

			if (m1.size() > 10000)
				m1.clear();

			try
			{
				Member member = memberDao.loadMember(memberId);
				m1.put(memberId, member);

				return member;
			}
			catch (Exception e)
			{
				Log.info("member not found - " + memberId);
				return null;
			}
		}
	}

	public Org getOrg(Long orgId)
	{
		synchronized (m2)
		{
			if (m2.containsKey(orgId))
				return m2.get(orgId);

			try
			{
				Org org = orgDao.loadOrg(orgId);
				m2.put(orgId, org);

				return org;
			}
			catch (Exception e)
			{
				Log.info("org not found - " + orgId);
				return null;
			}
		}
	}

	public Company getCompany(Long companyId)
	{
		synchronized (m3)
		{
			if (m3.containsKey(companyId))
				return m3.get(companyId);

			try
			{
				Company company = companyDao.loadCompany(companyId);
				m3.put(companyId, company);

				return company;
			}
			catch (Exception e)
			{
				Log.info("company not found - " + companyId);
				return null;
			}
		}
	}

	public Member findMember(Long userId)
	{
		try
		{
			return memberDao.findMember(userId);
		}
		catch (Exception e)
		{
			Log.info("not found - " + userId);
			return null;
		}
	}
}
