package lerrain.service.env;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class KeyValService
{
    public static final long TIME_OUT = 3600000L * 24;

    Map<String, Value> map = new HashMap<>();

    public void setValue(String key, Object value)
    {
        synchronized (map)
        {
            map.put(key, new Value(value));
        }
    }

    public Object getValue(String key)
    {
        Value val = map.get(key);
        val.time = System.currentTimeMillis();

        return val.value;
    }

    @Scheduled(cron = "2 0 0 * * ?")
    private void free()
    {
        synchronized (map)
        {
            Iterator<Map.Entry<String, Value>> iter = map.entrySet().iterator();
            while (iter.hasNext())
            {
                Map.Entry<String, Value> e = iter.next();
                if (System.currentTimeMillis() > e.getValue().time + TIME_OUT)
                    iter.remove();
            }
        }
    }

    public void store()
    {

    }

    public void restore()
    {

    }

    private static class Value
    {
        long time = System.currentTimeMillis();

        Object value;

        public Value(Object value)
        {
            this.value = value;
        }
    }
}

