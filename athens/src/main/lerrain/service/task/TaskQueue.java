package lerrain.service.task;

import lerrain.service.common.Log;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class TaskQueue extends lerrain.tool.TaskQueue<Task>
{
    @PostConstruct
    public void start()
    {
        super.start();
    }

    @Override
    public void perform(Task task)
    {
        try
        {
            Log.info(task.perform());
        }
        catch (Exception e)
        {
            Log.error(e);
        }
    }
}
