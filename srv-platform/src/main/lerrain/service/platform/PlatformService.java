package lerrain.service.platform;

import lerrain.service.platform.function.*;
import lerrain.tool.Common;
import lerrain.tool.formula.Factors;
import lerrain.tool.formula.Formula;
import lerrain.tool.formula.Function;
import lerrain.tool.script.Stack;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PlatformService
{
    @Autowired PlatformDao platformDao;
    @Autowired CacheService kvs;

    List<Platform> platforms;

    Map<Object, Platform> platform1, platform2;

    @Autowired NextId nextId;
    @Autowired Request request;
    @Autowired RequestPost post;
    @Autowired CallService service;
    @Autowired Fold fold;
    @Autowired Unfold unfold;

    @PostConstruct
    public void initiate()
    {
        Map stack = new HashMap();
        stack.put("nextId", nextId);
        stack.put("req", request);
        stack.put("post", post);
        stack.put("service", service);
        stack.put("jsonOf", new JsonOf());
        stack.put("fold", fold);
        stack.put("unfold", unfold);

        platforms = platformDao.loadChannels(stack);

        platform1 = new HashMap<>();
        platform2 = new HashMap<>();

        for (Platform c : platforms)
        {
            platform1.put(c.getId(), c);
            platform2.put(c.getCode(), c);
        }
    }

    public Platform getPlatform(Long platformId)
    {
        return platform1.get(platformId);
    }

    public Platform getPlatform(String channelCode)
    {
        return platform2.get(channelCode);
    }

    public int count(String search)
    {
        return platforms.size();
    }

    public List<Platform> list(String search)
    {
        return list(search, 0, -1);
    }

    public List<Platform> list(String search, int from, int number)
    {
        List<Platform> r = new ArrayList<>();
        for (int i = 0; i < (number < 0 ? 999 : number) && i < platforms.size(); i++)
            r.add(platforms.get(i + from));

        return r;
    }

    public Object perform(Platform platform, String operate, Map<String, Object> vals)
    {
        Stack stack = new Stack(platform.getEnv());
        stack.set("self", vals);

        return platform.getScript(operate).run(stack);
    }

    public Object callback(String key, Map map)
    {
        Map val = (Map)kvs.getValue(key);

        Function func = (Function)val.get("callback");
        Factors factors = (Factors)val.get("factors");

        return func.run(new Object[] {map, val.get("with")}, factors);
    }

}
