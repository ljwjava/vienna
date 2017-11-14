package lerrain.service.biz;

import lerrain.tool.Common;
import lerrain.tool.formula.Factors;
import lerrain.tool.formula.Formula;
import lerrain.tool.formula.Function;
import lerrain.tool.script.Stack;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class PlatformBizService
{
    @Autowired PlatformDao platformDao;
    @Autowired KeyValService kvs;
    @Autowired PlatformService platformService;

    public Object perform(Platform platform, Map<String, Object> vals)
    {
        if (platform == null)
            return null;

        if (platform.getPerform() == null)
            return null;

        return perform(platform, platform.getPerform(), vals);
    }

    public Object perform(Platform platform, Formula script, Map<String, Object> vals)
    {
        Stack stack = new Stack(platform.getEnv());
        stack.set("self", vals);
        stack.set("data", vals.get("content"));

        String env = (String)vals.get("env");
        if (!Common.isEmpty(env))
        {
            stack.declare("env");
            stack.set("env", env);
        }

        return script.run(stack);
    }


    public Object perform(Platform platform, String operate, Map<String, Object> vals)
    {
        Stack stack = new Stack(platform.getEnv());
        stack.set("self", vals);

        return platform.getScript(operate).run(stack);
    }

    public Object apply(Platform platform, Map<String, Object> vals)
    {
        Stack stack = new Stack(platform.getEnv());
        stack.set("self", vals);

        return platform.getApply().run(stack);
    }

    public Object verify(Platform platform, Map<String, Object> vals)
    {
        Stack stack = new Stack(platform.getEnv());
        stack.set("self", vals);

        return platform.getVerify().run(stack);
    }

    public Object callback(String key, Map map)
    {
        Map val = (Map)kvs.getValue(key);

        Function func = (Function)val.get("callback");
        Factors factors = (Factors)val.get("factors");

        Object params = val.get("with");
        return func.run(new Object[] {map, val.get("with")}, factors);

//        Platform platform = platformService.getPlatform(platformId);
//
//        Stack stack = new Stack(platform.getEnv());
//        stack.set("key", key);
//        stack.set("self", map);
//
//        return platform.getCallback().run(stack);
    }
}
