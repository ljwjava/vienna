package lerrain.service.env;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lerrain.service.env.function.*;
import lerrain.service.common.Log;
import lerrain.tool.Common;
import lerrain.tool.formula.Factors;
import lerrain.tool.formula.Function;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class EnvService
{
    @Autowired
    EnvDao envDao;

    List<Environment> environments;

    Map<Long, Environment> env1;
    Map<String, Environment> env2;

    @Autowired NextId nextId;
    @Autowired Request request;
    @Autowired RequestPost post;
    @Autowired CallService service;
    @Autowired Sql sql;
    @Autowired Fold fold;
    @Autowired Unfold unfold;
    @Autowired Dict dict;
    @Autowired TaskFX taskFX;
    @Autowired OnAlert alert;
    @Autowired StoreFX storeFX;

    Function time2long;
    Function today;
    Function timediff;
    Function reversalStr;
    Function log, err;

    @Value("${scope}")
    String scope;

    List<Long> envs = new ArrayList();

    public EnvService(){
        time2long = new Function() {
            @Override
            public Object run(Object[] v, Factors p) {
                if(v != null && v.length > 0){
                    Date t = Common.dateOf(v[0], new Date());
                    return t.getTime();
                }

                return new Date().getTime();
            }
        };
        today = new Function()
        {
            @Override
            public Object run(Object[] v, Factors p)
            {
                int windage = 0;
                if (v != null && v.length >= 1)
                    windage = Common.intOf(v[0], 0);

                if (windage == 0)
                    return String.format("%tF", new Date());
                else
                    return String.format("%tF", new Date(new Date().getTime() + windage * 3600000L * 24));
            }
        };
        reversalStr = new Function() {
            @Override
            public Object run(Object[] v, Factors p) {
                String s = Common.trimStringOf(v[0]);
                if(s == null){
                    return null;
                }
                StringBuilder b = new StringBuilder(s);
                return b.reverse();
            }
        };
        timediff = new Function()
        {
            @Override
            public Object run(Object[] v, Factors p)
            {
                if (v != null)
                {
                    Date eTime = Common.dateOf(v[0]);	// 往后时间
                    Date sTime = Common.dateOf(v[1]);	// 往前时间
                    String t = "day";	// year,month,day,hour,min,sec

                    if(v.length == 3 && v[2] != null) {
                        t = String.valueOf(v[2]).trim();
                        if(",year,month,day,hour,min,sec,".indexOf(","+t+",") < 0){
                            t = "day";
                        }
                    }
                    // 只计算天数
                    if(!"day".equalsIgnoreCase(t)){
                        return null;
                    }

                    long f = 3600000L * 24;	// 秒
                    long end = eTime.getTime() / f;
                    long start = sTime.getTime() / f;

                    return end - start;
                }

                return null;
            }
        };
        log = new Function()
        {
            @Override
            public Object run(Object[] objects, Factors factors)
            {
                Log.debug(objects[0]);
                return null;
            }
        };
        err = new Function()
        {
            @Override
            public Object run(Object[] objects, Factors factors)
            {
                Log.error(objects[0]);
                return null;
            }
        };
    }

    public void reset()
    {
        envDao.loadDbSource();

        Map stack = new HashMap();
        stack.put("log", log);
        stack.put("err", err);
        stack.put("alert", alert);
        stack.put("run", new RunFunction());
        stack.put("copy", new Copy());
        stack.put("stackOf", new StackOf());
        stack.put("nextId", nextId);
        stack.put("req", request);
        stack.put("post", post);
        stack.put("Sql", sql);
        stack.put("service", service);
        stack.put("dict", dict);
        stack.put("sort", new Sort());
        stack.put("jsonMap", new JsonMap());
        stack.put("jsonList", new JsonList());
        stack.put("jsonOf", new JsonOf());
        stack.put("jsonStr", new JsonStr());
        stack.put("fold", fold);
        stack.put("unfold", unfold);
        stack.put("today", today);
        stack.put("time2long", time2long);  // 已移至TimeFX.long
        stack.put("timediff", timediff);    // 已移至TimeFX.diff
        stack.put("reversalStr", reversalStr);
        stack.put("md5Of", new Md5());
        stack.put("urlParam", new UrlParam());  // 已移至UrlFX.linkOf
        stack.put("Encrypt", new Encrypt());
        stack.put("TimeFX", new TimeFX());
        stack.put("FileFX", new FileFX());
        stack.put("ToolFX", new ToolFX());
        stack.put("UrlFX", new UrlFX());
        stack.put("TaskFX", taskFX);
        stack.put("StoreFX", storeFX);
        stack.put("IYunBao", new IYunBao());

        environments = envDao.loadAllEnv(stack);

        env1 = new HashMap<>();
        env2 = new HashMap<>();

        for (Environment c : environments)
        {
            env1.put(c.getId(), c);
            env2.put(c.getCode(), c);
        }

        for (String str : scope.split(","))
        {
            if (str.endsWith("++"))
            {
                Environment env = env2.get(str.substring(0, str.length() - 2));
                if (env != null) for (Environment c : environments)
                {
                    Environment e = c;

                    do
                    {
                        if (env.getId().equals(e.getId()))
                        {
                            if (envs.indexOf(c.getId()) < 0)
                                envs.add(c.getId());

                            break;
                        }

                        e = env1.get(e.getParentId());
                    }
                    while (e != null);
                }
            }
            else if (str.endsWith("+"))
            {
                Environment env = env2.get(str.substring(0, str.length() - 2));
                if (env != null) for (Environment c : environments)
                {
                    if (env.getId().equals(c.getId()) || env.getId().equals(c.getParentId()))
                    {
                        if (envs.indexOf(c.getId()) < 0)
                            envs.add(c.getId());
                    }
                }
            }
            else
            {
                Environment env = env2.get(str);

                if (env != null)
                    envs.add(env.getId());
            }
        }
    }

    public List<Environment> list()
    {
        return environments;
    }

    public Environment getEnv(Long envId)
    {
        return env1.get(envId);
    }

    public boolean isValid(Long envId)
    {
        return envs.indexOf(envId) >= 0;
    }

    public Environment getEnv(String envCode)
    {
        return env2.get(envCode);
    }

    public int count(String search)
    {
        return environments.size();
    }

    public List<Environment> list(String search)
    {
        return list(search, 0, -1);
    }

    public List<Environment> list(String search, int from, int number)
    {
        List<Environment> r = new ArrayList<>();
        for (int i = 0; i < (number < 0 ? 999 : number) && i < environments.size(); i++)
            r.add(environments.get(i + from));

        return r;
    }
}
