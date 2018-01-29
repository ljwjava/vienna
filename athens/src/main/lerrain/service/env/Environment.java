package lerrain.service.env;

import lerrain.tool.formula.Formula;
import lerrain.tool.script.Stack;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lerrain on 2017/5/18.
 */
public class Environment
{
    Long id;
    Long parentId;

    String code;
    String name;
    String[] refer;

    Stack stack;
    Formula init;

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

    public Long getParentId()
    {
        return parentId;
    }

    public void setParentId(Long parentId)
    {
        this.parentId = parentId;
    }

    public String[] getRefer()
    {
        return refer;
    }

    public void setRefer(String[] refer)
    {
        this.refer = refer;
    }

    public Stack getStack()
    {
        return stack;
    }

    public void setStack(Stack stack)
    {
        this.stack = stack;
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
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

    public Formula getInit()
    {
        return init;
    }

    public void setInit(Formula init)
    {
        this.init = init;
    }

    public void putVar(String fe, Object f)
    {
        Map last = null;

        String[] name = fe.split("[.]");
        for (int i=0;i<name.length-1;i++)
        {
            Map map = (Map)(i == 0 ? stack.get(name[i]) : last.get(name[i]));
            if (map == null)
            {
                map = new HashMap();
                if (i == 0)
                    stack.declare(name[i], map);
                else
                    last.put(name[i], map);
            }

            last = map;
        }

        if (last == null)
            stack.declare(name[name.length - 1], f);
        else
            last.put(name[name.length - 1], f);
    }
}
