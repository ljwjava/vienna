package lerrain.service.org;

import lerrain.service.common.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrgService
{
	@Autowired
	MemberDao memberDao;

	public Member getMember(Long memberId)
	{
		return memberDao.loadMember(memberId);
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
