package lerrain.service.org;

import java.util.List;
import java.util.Map;

import lerrain.service.common.ServiceTools;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 类MemberService.java的实现描述：子账户服务
 * 
 * @author iyb-wangguangrong 2018年5月11日 上午10:48:45
 */
@Service
public class MemberService
{
	@Autowired
	MemberDao memberDao;
    @Autowired
    ServiceTools tools;


    public Long save(Member member) {
        // 主键ID与userId保持一致
        Long id = tools.nextId("user");
        member.setId(id);
        int result = memberDao.save(member);
        if (result > 0) {
            return id;
        }
        return null;
	}

    public boolean update(Member member) {
        return memberDao.update(member);
    }

    public Long countByOrgId(Long orgId) {
        return memberDao.countByOrgId(orgId);
    }

    public List<Map<String, Object>> listByOrgId(Long orgId, int from, int number, String searchCondition) {
        return memberDao.listByOrgId(orgId, from, number, searchCondition);
    }
}
