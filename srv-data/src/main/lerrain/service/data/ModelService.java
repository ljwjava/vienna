package lerrain.service.data;

import lerrain.tool.script.Stack;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ModelService
{
	@Autowired
	ModelDao modelDao;

	Map<Long, Model> map = new HashMap<>();

	Map<String, List<Model>> taskMap = new HashMap<>();

	public void reset(Map<Object, Stack> envMap)
	{
		map.clear();
		taskMap.clear();

		for (Model model : modelDao.loadAllModel(envMap))
		{
			map.put(model.getId(), model);

			if (model.getInvoke() != null)
			{
				List<Model> list = taskMap.get(model.getInvoke());

				if (list == null)
				{
					list = new ArrayList<>();
					taskMap.put(model.getInvoke(), list);
				}

				list.add(model);
			}
		}
	}
	
	public List<Model> getTask(String invoke)
	{
		return taskMap.get(invoke);
	}

}
