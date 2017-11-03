package lerrain.service.data.source.arcturus;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lerrain.tool.Common;
import lerrain.tool.Disk;

import java.io.*;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * Created by lerrain on 2017/9/27.
 */
public class ArcMap implements Map<Long, Map>
{
    public final static long K1        = 250L;
    public final static long K2        = 250L;
    public final static long K3        = 250L;

    private static final int MAX_CACHE = 10000;

    String root;
    String name;

    String primary;

    public ArcMap(String root, String name)
    {
        this.root = root;
        this.name = name;

        File f = new File(Common.pathOf(root, name));
        if (!f.exists())
            f.mkdirs();

        File config = new File(Common.pathOf(root, name + ".prop"));
        if (config.exists())
        {
            Properties prop = new Properties();
            try (FileInputStream is = new FileInputStream(config))
            {
                prop.load(is);
            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }

            primary = prop.getProperty("primary");
        }
        else
        {
            primary = "primary";

            Properties prop = new Properties();
            prop.put("primary", primary);

            try (FileOutputStream fos = new FileOutputStream(config))
            {
                prop.store(fos, null);
            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public int size()
    {
        return 0;
    }

    @Override
    public boolean isEmpty()
    {
        return false;
    }

    @Override
    public boolean containsKey(Object key)
    {
        String path = getPath(key);

        synchronized (this)
        {
            File f = new File(path);
            return f.exists();
        }
    }

    @Override
    public boolean containsValue(Object value)
    {
        throw new RuntimeException("not support");
    }

    @Override
    public Map get(Object key)
    {
        String path = getPath(key);

        synchronized (this)
        {
            JSONObject json = JSON.parseObject(Disk.load(new File(path + "/" + primary), "utf-8"));
            return new ArcDoc(this, path, json);
        }
    }

    public String getPath(Object key)
    {
        long kk = (Long)key;
        long k1 = kk % ArcMap.K1;
        kk /= ArcMap.K1;
        long k2 = kk % ArcMap.K2;
        kk /= ArcMap.K2;
        long k3 = kk % ArcMap.K3;
        kk /= ArcMap.K3;

        return Common.pathOf(root, name, k1 + "/" + k2 + "/" + k3 + "/" + kk);
    }

    @Override
    public Map put(Long key, Map value)
    {
        String path = getPath(key);

        synchronized (this)
        {
            new File(path).mkdirs();

            File f = new File(path + "/" + primary);
            try (OutputStream os = new FileOutputStream(f))
            {
                os.write(JSON.toJSONString(value).getBytes("utf-8"));
            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }
        }

        return null;
    }

    @Override
    public JSONObject remove(Object key)
    {
        throw new RuntimeException("not support");
    }

    @Override
    public void putAll(Map<? extends Long, ? extends Map> m)
    {
        for (Map.Entry<? extends Long, ? extends Map> e : m.entrySet())
            put(e.getKey(), e.getValue());
    }

    @Override
    public void clear()
    {
        throw new RuntimeException("not support");
    }

    @Override
    public Set<Long> keySet()
    {
        return new ArcKeySet(this);
    }

    @Override
    public Collection<Map> values()
    {
        return new ArcDocCol(this);
    }

    @Override
    public Set<Entry<Long, Map>> entrySet()
    {
        throw new RuntimeException("not support");
    }
}
