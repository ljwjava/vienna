package lerrain.service.data2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TaskManager
{
	@Autowired
	TaskService taskSrv;

	@Scheduled(cron = "0 0 8 * * ?")
	public void day08()
	{
		invoke("day/08:00");
	}

	@Scheduled(cron = "0 0 7 * * ?")
	public void day07()
	{
		invoke("day/07:00");
	}

	@Scheduled(cron = "0 0 6 * * ?")
	public void day06()
	{
		invoke("day/06:00");
	}

	@Scheduled(cron = "0 0 5 * * ?")
	public void day05()
	{
		invoke("day/05:00");
	}

	@Scheduled(cron = "0 0 4 * * ?")
	public void day04()
	{
		invoke("day/04:00");
	}

	@Scheduled(cron = "0 0 3 * * ?")
	public void day03()
	{
		invoke("day/03:00");
	}

	@Scheduled(cron = "0 0 2 * * ?") 
	public void day02()
	{
		invoke("day/02:00");
	}

	@Scheduled(cron = "0 0 1 * * ?") 
	public void day01()
	{
		invoke("day/01:00");
	}

	@Scheduled(cron = "0 0 0 * * ?") 
	public void day00()
	{
		invoke("day/00:00");
	}
	
	@Scheduled(cron = "0 0/1 * * * ?") 
	public void minute()
	{
		invoke("minute");
	}

	@Scheduled(cron = "0 0/10 * * * ?") 
	public void minute10()
	{
		invoke("minute/10");
	}
	
	@Scheduled(cron = "0 0/30 * * * ?") 
	public void minute30()
	{
		invoke("minute/30");
	}

	@Scheduled(cron = "0 0 0/1 * * ?") 
	public void hour()
	{
		invoke("hour");
	}

	private void invoke(String invokeTime)
	{
		for (Task task : taskSrv.findTaskByInvokeTime(invokeTime))
			taskSrv.run(task.getId(), null);
	}
}
