package lerrain.service.env;

import lerrain.service.common.Log;
import lerrain.tool.Common;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;

@Service
public class KeyValService
{
    public static final long TIME_OUT = 3600000L * 24;

    public static final TimeValue NOT_FOUND = new TimeValue(null);

    @Value("${path.temp}")
    String temp;

    Map<String, TimeValue> last = null;

    Map<String, TimeValue> map = new HashMap<>();

    public void setValue(String key, Object value)
    {
        synchronized (map)
        {
            map.put(key, new TimeValue(value));
        }
    }

    public Object getValue(String key)
    {
        TimeValue val;

        synchronized (map)
        {
            if (map.containsKey(key))
            {
                val = map.get(key);
            }
            else
            {
                if (last == null)
                {
                    File f = new File(Common.pathOf(temp, "keyval.obj"));
                    if (!f.exists())
                    {
                        last = new HashMap();
                    }
                    else
                    {
                        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f)))
                        {
                            last = (Map) ois.readObject();
                        }
                        catch (Exception e)
                        {
                            Log.error(e);
                            last = new HashMap();
                        }
                    }
                }

                if (last.containsKey(key))
                {
                    val = last.get(key);
                }
                else
                {
                    val = NOT_FOUND;
                }
            }
        }

        if (val == NOT_FOUND) //遍历其他的athens实例获取
        {

        }

        val.time = System.currentTimeMillis();

        return val.value;
    }

    @Scheduled(cron = "2 0 0 * * ?")
    private void free()
    {
        synchronized (map)
        {
            Iterator<Map.Entry<String, TimeValue>> iter = map.entrySet().iterator();
            while (iter.hasNext())
            {
                Map.Entry<String, TimeValue> e = iter.next();
                if (System.currentTimeMillis() > e.getValue().time + TIME_OUT)
                    iter.remove();
            }
        }
    }

    public void store()
    {
        synchronized (map)
        {
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File(Common.pathOf(temp, "keyval.obj")))))
            {
                oos.writeObject(map);
            }
            catch (Exception e)
            {
                Log.error(e);
            }
        }
    }

    private static class TimeValue
    {
        long time = System.currentTimeMillis();

        Object value;

        public TimeValue(Object value)
        {
            this.value = value;
        }
    }
}

