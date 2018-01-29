package lerrain.service.data;

import lerrain.tool.formula.Formula;
import lerrain.tool.script.Stack;

public class Task
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

    public Object perform()
    {
        return script.run(stack);
    }
}
