package lerrain.service.data.source.arcturus;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import lerrain.tool.Common;
import lerrain.tool.Disk;

import java.io.*;
import java.util.*;

/**
 * Created by lerrain on 2017/9/27.
 */
public class ArcMap implements Map<Long, Map>
{
    public final static long K1        = 250L;
    public final static long K2        = 250L;
    public final static long K3        = 250L;

    String root;
    String name;

    String primary;

    JSONObject files;
    JSONObject index;

    public ArcMap(String root, String name)
    {
        this.root = root;
        this.name = name;

        File f = new File(Common.pathOf(root, name));
        if (!f.exists())
            f.mkdirs();

        File config = new File(Common.pathOf(root, name + ".json"));
        if (config.exists())
        {
            JSONObject prop = JSON.parseObject(Disk.load(config, "utf-8"));

            primary = prop.getString("primary");
            files = prop.getJSONObject("files");
            index = prop.getJSONObject("index");
        }
        else
        {
            primary = "primary";

            JSONObject prop = new JSONObject();
            prop.put("primary", primary);

            try (ByteArrayInputStream is = new ByteArrayInputStream(prop.toJSONString().getBytes("utf-8")))
            {
                Disk.saveToDisk(is, config);
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
        throw new RuntimeException("not support");
    }

    @Override
    public boolean isEmpty()
    {
        return false;
    }

    @Override
    public boolean containsKey(Object key)
    {
        String path = getPath((Long)key);

        File f = new File(path);
        return f.exists();
    }

    @Override
    public boolean containsValue(Object value)
    {
        throw new RuntimeException("not support");
    }

    @Override
    public Map get(Object key)
    {
        String path = getPath((Long)key);
        String file = path + "/" + primary;

        byte[] b = ArcTool.text.map.get(file);

        if (b == null)
            b = ArcTool.text.pack.get(file);

        if (b == null)
            b = Disk.load(new File(file));

        JSONObject json = (JSONObject)JSON.parse(b, Feature.AllowUnQuotedFieldNames);
        return new ArcDoc(this, path, json);
    }

    public String getPath(Long key)
    {
        long kk = key.longValue();
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

        ArcTool.text.push(path + "/" + primary, JSON.toJSONBytes(value));

        if (value != null && index != null) for (Entry<String, Object> e : index.entrySet())
        {
            JSONArray list = (JSONArray)JSON.toJSON(e.getValue());
            for (Object k : list)
            {
                Object val = value.get(k);
                if (val != null)
                {
                    String valStr = val.toString();
                    String valPath = getPath(ArcTool.getSign(valStr));

                    ArcTool.seek.push(valPath + "/" + e.getKey() + "." + valStr, key);
                }
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
