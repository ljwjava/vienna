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
public class TaskService
{
	@Autowired
	TaskDao taskDao;

	@Autowired
	DataService dataSrv;

	Map<Long, Task> taskMap;

	public void reset()
	{
		taskMap = taskDao.loadAllTask();
	}
	
	public Task getTask(Long taskId)
	{
		return taskMap.get(taskId);
	}

	public List<Task> findTaskByInvokeTime(String invokeTime)
	{
		List<Task> list = new ArrayList<>();
		for (Task task : taskMap.values())
		{
			if (invokeTime.equals(task.getInvoke()))
				list.add(task);
		}

		return list;
	}

	public Object run(Long taskId, Map val)
	{
		Task f = getTask(taskId);

		Stack s = new Stack(dataSrv.getEnv(f.getEnvId()));
		if (val != null)
			s.setAll(val);

		return f.getScript().run(s);
	}

}
