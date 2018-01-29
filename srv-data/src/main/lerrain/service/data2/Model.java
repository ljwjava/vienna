package lerrain.service.data2;

import lerrain.tool.formula.Formula;
import lerrain.tool.script.Stack;

import java.util.Map;

/**
 * Created by lerrain on 2017/9/19.
 */
public class Model
{
    Long id;
    Long sortId;

    String name;
    String invoke;

    Stack env;
    Formula script;

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

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

    public Stack getEnv()
    {
        return env;
    }

    public void setEnv(Stack env)
    {
        this.env = env;
    }

    public Long getSortId()
    {
        return sortId;
    }

    public void setSortId(Long sortId)
    {
        this.sortId = sortId;
    }

    public Object run(Map map)
    {
        if (map == null)
            return run();

        Stack stack = new Stack(env);
        stack.setAll(map);

        return script.run(stack);
    }

    public Object run()
    {
        return script.run(env);
    }
}
