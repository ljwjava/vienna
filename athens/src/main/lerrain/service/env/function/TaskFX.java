package lerrain.service.env.function;

import com.alibaba.fastjson.JSON;
import lerrain.service.task.Task;
import lerrain.service.task.TaskQueue;
import lerrain.tool.Common;
import lerrain.tool.formula.Factors;
import lerrain.tool.formula.Function;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by lerrain on 2017/5/19.
 */
@Service
public class TaskFX extends HashMap<String, Object>
{
    @Autowired
    TaskQueue queue;

    public TaskFX()
    {
        this.put("add", new Function()
        {
            @Override
            public Object run(final Object[] objects, final Factors factors)
            {
                queue.add(new Task()
                {
                    @Override
                    public Object perform()
                    {
                        Function f = (Function)objects[0];
                        return f.run((Object[])(objects.length >= 1 ? objects[1] : null), factors);
                    }
                });

                return null;
            }
        });
    }
}
