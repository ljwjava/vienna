package lerrain.service.platform;

import lerrain.tool.formula.Formula;
import lerrain.tool.script.Stack;

import java.util.Date;
import java.util.Map;

/**
 * Created by lerrain on 2017/5/18.
 */
public class Platform
{
    Long id;

    String code;
    String name;

    Stack env;

    Map<String, Formula> scripts;

    Date createTime;
    Date updateTime;

    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

    public Stack getEnv()
    {
        return env;
    }

    public void setEnv(Stack env)
    {
        this.env = env;
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Formula getScript(String name)
    {
        return scripts.get(name);
    }

    public void setScripts(Map<String, Formula> scripts)
    {
        this.scripts = scripts;
    }

    public Date getCreateTime()
    {
        return createTime;
    }

    public void setCreateTime(Date createTime)
    {
        this.createTime = createTime;
    }

    public Date getUpdateTime()
    {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime)
    {
        this.updateTime = updateTime;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }
}
