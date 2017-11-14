package lerrain.service.biz;

import lerrain.service.biz.function.*;
import lerrain.service.common.Log;
import lerrain.service.biz.function.*;
import lerrain.tool.Common;
import lerrain.tool.formula.Factors;
import lerrain.tool.formula.Function;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PlatformService
{
    @Autowired
    PlatformDao platformDao;

    List<Platform> platforms;

    Map<Object, Platform> platform1, platform2;

    @Autowired CallLife callLife;
    @Autowired NextId2 nextId;
    @Autowired Request request;
    @Autowired RequestPost post;
    @Autowired CallService service;
    @Autowired Sql sql;
    @Autowired Fold fold;
    @Autowired Unfold unfold;

    Function today;
    Function timediff;
    Function reversalStr;
    Function getAge;
    Function log, err;

    public PlatformService(){
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
        getAge = new Function() {
            @Override
            public Object run(Object[] v, Factors factors) {
                if(v == null || v.length <= 0){
                    return -1;
                }
                Date fDate = new Date();
                if(v.length >= 2){
                    fDate = Common.dateOf(v[1], new Date());
                }

                return Common.getAge(Common.dateOf(v[0]), fDate);
            }
        };
    }

    public void reset()
    {
        Map stack = new HashMap();
        stack.put("log", log);
        stack.put("err", err);
        stack.put("biz", callLife);
        stack.put("life", callLife);
        stack.put("nextId", nextId);
        stack.put("req", request);
        stack.put("post", post);
        stack.put("Sql", sql);
        stack.put("service", service);
        stack.put("jsonMap", new JsonMap());
        stack.put("jsonList", new JsonList());
        stack.put("jsonOf", new JsonOf());
        stack.put("fold", fold);
        stack.put("unfold", unfold);
        stack.put("today", today);
        stack.put("timediff", timediff);
        stack.put("reversalStr", reversalStr);
        stack.put("md5Of", new Md5());
        stack.put("urlParam", new UrlParam());
        stack.put("Encrypt", new Encrypt());
        stack.put("getAge", getAge);

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
}



