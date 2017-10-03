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

	Map<Object, Role> roles;

	@PostConstruct
	public void reset()
	{
		roles = new HashMap<Object, Role>();
		
		List<Role> roleList = roleDao.loadAll();
		for (Role role : roleList)
		{
			roles.put(role.getId(), role);
			roles.put(role.getCode(), role);
		}
	}
	
	public Role getRole(Long roleId)
	{
		return roles.get(roleId);
	}

	public Role getRole(String roleCode)
	{
		return roles.get(roleCode);
	}
}
