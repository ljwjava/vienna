package lerrain.service.sale;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class KeyValService
{
    public static final long TIME_OUT = 7200000;

    Map<String, Long> time = new HashMap<String, Long>(); // 最后刷新时间
    Map map = new HashMap();

    public void setValue(String key, Object value)
    {
        synchronized (map) {
            map.put(key, value);
        }
        synchronized (time){
            time.put(key, System.currentTimeMillis());
        }
    }

    public Object getValue(String key)
    {
        synchronized (time){
            time.put(key, System.currentTimeMillis());
        }
        return map.get(key);
    }

    @Scheduled(cron = "0 0/30 * * * ?")
    private void free(){
        List<String> l = new ArrayList<String>();
        for (Map.Entry<String, Long> e : time.entrySet())
        {
            if (System.currentTimeMillis() > e.getValue() + TIME_OUT)
            {
                l.add(e.getKey());
            }
        }

        for (String k: l) {
            if(time.get(k) != null && System.currentTimeMillis() > time.get(k) + TIME_OUT) {
                synchronized (time){
                    time.remove(k);
                }
                synchronized (map){
                    map.remove(k);
                }
            }
        }
    }

}
