package lerrain.service.task;

import lerrain.service.common.Log;
import lerrain.tool.formula.Formula;
import lerrain.tool.script.Stack;

public class TimingTask implements Runnable
{
    String invoke;

    Stack stack;

    Formula script;

    public String getInvoke()
    {
        return invoke;
    }

    public void setInvoke(String invoke)
    {
        this.invoke = invoke;
    }

    public Formula getScript()
    {
        return script;
    }

    public void setScript(Formula script)
    {
        this.script = script;
    }

    public Stack getStack()
    {
        return stack;
    }

    public void setStack(Stack stack)
    {
        this.stack = stack;
    }

    @Override
    public void run()
    {
        Object val = script.run(stack);
        Log.info("TASK ON TIME -- " + val);
    }
}
