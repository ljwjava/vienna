package lerrain.service.sale;

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

    Formula perform;
    Formula verify;
    Formula apply;
    Formula callback;

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

    public Formula getApply()
    {
        return apply;
    }

    public Formula getScript(String name)
    {
        return scripts.get(name);
    }

    public void setScripts(Map<String, Formula> scripts)
    {
        this.scripts = scripts;
    }

    public void setApply(Formula apply)
    {
        this.apply = apply;
    }

    public Formula getPerform()
    {
        return perform;
    }

    public void setPerform(Formula perform)
    {
        this.perform = perform;
    }

    public Formula getVerify()
    {
        return verify;
    }

    public void setVerify(Formula verify)
    {
        this.verify = verify;
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

    public Formula getCallback()
    {
        return callback;
    }

    public void setCallback(Formula callback)
    {
        this.callback = callback;
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
