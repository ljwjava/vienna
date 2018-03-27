package lerrain.service.env.function;

import lerrain.service.biz.GatewayService;
import lerrain.service.env.ScriptErrorException;
import lerrain.service.task.Task;
import lerrain.service.task.TaskQueue;
import lerrain.tool.Common;
import lerrain.tool.formula.Factors;
import lerrain.tool.formula.Function;
import lerrain.tool.script.Stack;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;


/**
 * Created by lerrain on 2017/5/19.
 */
@Service
public class TaskFX extends HashMap<String, Object>
{
    @Autowired
    TaskQueue queue;

    @Autowired
    GatewayService gatewaySrv;

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
                        try
                        {
                            Function f = (Function) objects[0];
                            return f.run((Object[]) (objects.length > 1 ? objects[1] : null), factors);
                        }
                        catch (ScriptErrorException e1)
                        {
                            gatewaySrv.onError(e1.getFactors(), e1.getMessage(), e1.getReferNo(), e1);

                            throw e1;
                        }
                        catch (Exception e)
                        {
                            gatewaySrv.onError(factors, "临时任务执行失败", null, e);

                            throw e;
                        }
                    }
                });

                return null;
            }
        });
    }
}
