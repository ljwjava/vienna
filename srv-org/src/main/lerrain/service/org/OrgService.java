package lerrain.service.org;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lerrain.service.common.Log;
import lerrain.service.common.ServiceTools;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

@Service
public class OrgService
{
	@Autowired
	MemberDao memberDao;

	@Autowired
	OrgDao orgDao;

	@Autowired
	CompanyDao companyDao;

    @Autowired
    ServiceTools       tools;

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

    public Object findMember(Long userId, int userType) {
        try {
            // 判断用户是部门还是子账户
            if (2 == userType) {
                return orgDao.loadOrg(userId);
            } else {
                return memberDao.findMember(userId);
            }
        } catch (Exception e) {
            Log.info("not found - " + userId);
            return null;
        }
    }

    public Long save(Org org) {
        // 主键ID与userId保持一致
        Long id = tools.nextId("user");
        org.setId(id);
        int result = orgDao.save(org);
        if (result > 0) {
            return id;
        }
        return null;
    }

    public boolean update(Org org) {
        return orgDao.update(org);
    }

    public boolean remove(Long id) {
        return orgDao.remove(id);
    }

    public List<Org> childOrg(Long id) {
        return orgDao.childOrg(id);
    }

    public JSONArray orgTree(Long id) {
        JSONArray array = new JSONArray();
        JSONObject result = new JSONObject();
        array.add(result);
        Org currentOrg = orgDao.loadOrg(id);
        result.put("key", currentOrg.getId());
        result.put("title", currentOrg.getName());
        // 获取子机构
        List<Org> list = orgDao.querySubordinateById(currentOrg.getId());
        JSONArray listArray = new JSONArray();
        if (list != null && list.size() > 0) {
            for (Org org : list) {
                listArray.add(parseOrgTree(org));
            }
        }
        if (listArray != null && listArray.size() > 0) {
            result.put("children", listArray);
        }
        return array;
    }

    private JSONObject parseOrgTree(Org org) {
        JSONObject result = new JSONObject();
        result.put("key", org.getId());
        result.put("title", org.getName());
        List<Org> list = orgDao.querySubordinateById(org.getId());
        JSONArray listArray = new JSONArray();
        if (list != null && list.size() > 0) {
            for (Org orgs : list) {
                listArray.add(parseOrgTree(orgs));
            }
        }
        if (listArray != null && listArray.size() > 0) {
            result.put("children", listArray);
        }
        return result;
    }

}
