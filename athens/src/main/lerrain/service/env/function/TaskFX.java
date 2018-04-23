package lerrain.service.env.function;

import lerrain.service.biz.GatewayService;
import lerrain.service.common.Log;
import lerrain.service.env.ScriptErrorException;
import lerrain.service.task.TaskQueue;
import lerrain.tool.formula.Factors;
import lerrain.tool.formula.Function;
import lerrain.tool.script.ScriptRuntimeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

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
                String key = objects.length > 2 ? objects[2].toString() : null;
                queue.add(key, new Runnable()
                {
                    @Override
                    public void run()
                    {
                        try
                        {
                            Function f = (Function) objects[0];
                            Object val = f.run((Object[]) (objects.length > 1 ? objects[1] : null), factors);

                            Log.info("TASK -- " + f.toString() + " -- " + val);
                        }
                        catch (ScriptErrorException e1)
                        {
                            gatewaySrv.onError(e1.getFactors(), e1.getMessage(), e1.getReferNo(), e1);

                            throw e1;
                        }
                        catch (ScriptRuntimeException e)
                        {
                            gatewaySrv.onError(e.getFactors(), e.toStackString(), null, e);

                            throw e;
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
