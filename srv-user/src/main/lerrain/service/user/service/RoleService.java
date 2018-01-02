package lerrain.service.user.service;

import lerrain.service.user.Role;
import lerrain.service.user.dao.RoleDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RoleService
{
	@Autowired
	RoleDao roleDao;

	List<Role> roleList;
	Map<Object, Role> roleMap;

	@PostConstruct
	public void reset()
	{
		roleMap = new HashMap<Object, Role>();
		
		roleList = roleDao.loadAll();
		for (Role role : roleList)
		{
			roleMap.put(role.getId(), role);
			roleMap.put(role.getCode(), role);
		}
	}

	public List<Role> getRoleList()
	{
		return roleList;
	}
	
	public Role getRole(Long roleId)
	{
		return roleMap.get(roleId);
	}

	public Role getRole(String roleCode)
	{
		return roleMap.get(roleCode);
	}
}
