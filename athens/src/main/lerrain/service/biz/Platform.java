package lerrain.service.biz;

import lerrain.tool.formula.Formula;
import lerrain.tool.script.Stack;

import java.util.Date;
import java.util.HashMap;
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

    public void putVar(String fe, Object f)
    {
        Map last = null;

        String[] name = fe.split("[.]");
        for (int i=0;i<name.length-1;i++)
        {
            Map map = (Map)(i == 0 ? env.get(name[i]) : last.get(name[i]));
            if (map == null)
            {
                map = new HashMap();
                if (i == 0)
                    env.set(name[i], map);
                else
                    last.put(name[i], map);
            }

            last = map;
        }

        if (last == null)
            env.set(name[name.length - 1], f);
        else
            last.put(name[name.length - 1], f);
    }
}
