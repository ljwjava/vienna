package lerrain.service.user.service;

import lerrain.service.user.Module;
import lerrain.service.user.Role;
import lerrain.service.user.dao.ModuleDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

@Service
public class ModuleService
{
	@Autowired
	ModuleDao moduleDao;

	Map<Object, Module> modules;

	Map<Role, List<Module>> temp = new HashMap<>();

	@PostConstruct
	public void reset()
	{
		modules = new HashMap<>();

		for (Module m : moduleDao.loadAll())
		{
			modules.put(m.getId(), m);

			if (m.getCode() != null)
				modules.put(m.getCode(), m);
		}
	}
	
	public Module getModule(Long moduleId)
	{
		return modules.get(moduleId);
	}

	public List<Module> findModules(List<Role> list)
	{
		List<Module> r = new ArrayList<>();

		for (Role role : list)
		{
			List<Module> t = temp.get(role);

			if (t == null)
			{
				t = moduleDao.getRoleModule(role, modules);
				temp.put(role, t);
			}

			if (t != null) for (Module m : t)
			{
				if (m.getParentId() != null && r.indexOf(m) < 0)
					r.add(m);
			}
		}

		Collections.sort(r, new Comparator<Module>()
		{
			@Override
			public int compare(Module o1, Module o2)
			{
				if (o1.getParentId().equals(o2.getParentId()))
					return o1.getSequence() < o2.getSequence() ? -1 : 1;
				else
					return getModule(o1.getParentId()).getSequence() < getModule(o2.getParentId()).getSequence() ? -1 : 1;
			}
		});

		return r;
	}
}
