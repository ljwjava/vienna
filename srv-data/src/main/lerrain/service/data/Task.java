package lerrain.service.data;

import lerrain.tool.formula.Formula;
import lerrain.tool.script.Stack;

/**
 * Created by lerrain on 2017/9/21.
 */
public class Task
{
    Long id;
    Long envId;

    String code;
    String invoke;

    Formula script;

    public Long getEnvId()
    {
        return envId;
    }

    public void setEnvId(Long envId)
    {
        this.envId = envId;
    }

    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
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
}
