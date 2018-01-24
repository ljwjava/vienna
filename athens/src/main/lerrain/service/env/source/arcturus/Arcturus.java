package lerrain.service.env.source.arcturus;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import lerrain.tool.Common;
import lerrain.tool.Disk;

import java.io.File;
import java.util.Map;

/**
 * Created by lerrain on 2017/9/27.
 */
public class Arcturus
{
    public final static long K1        = 4L;
    public final static long K2        = 250L;
    public final static long K3        = 250L;

    String[] root;
    String name;

    String content;

    Map<String, Object> files;
    Map<String, Object> index;

    public Arcturus(String[] root, String name)
    {
        this(root, name, "content", null, null);
    }

    public Arcturus(String[] root, String name, String content, Map files, Map index)
    {
        this.root = root;
        this.name = name;

        this.content = content;
        this.files = files;
        this.index = index;

        File config = new File(Common.pathOf(root[0], name + ".json"));
        if (config.exists())
        {
            JSONObject prop = JSON.parseObject(Disk.load(config, "utf-8"));

            this.content = prop.getString("content");
            this.files = prop.getJSONObject("files");
            this.index = prop.getJSONObject("index");
        }
        else
        {
//            JSONObject prop = new JSONObject();
//            prop.put("content", this.content);
//
//            try (ByteArrayInputStream is = new ByteArrayInputStream(prop.toJSONString().getBytes("utf-8")))
//            {
//                Disk.saveToDisk(is, config);
//            }
//            catch (Exception e)
//            {
//                throw new RuntimeException(e);
//            }
        }
    }

    public boolean has(long key)
    {
        String path = getPath(key);

        File f = new File(path);
        return f.exists();
    }

    public Map get(long key)
    {
        String path = getPath(key);
        String file = path + "/" + content;

        byte[] b = ArcTool.text.map.get(file);

        if (b == null)
            b = ArcTool.text.pack.get(file);

        if (b == null)
        {
            File f = new File(file);
            if (!f.exists())
                return null;

            b = Disk.load(f);
        }

        JSONObject json = (JSONObject)JSON.parse(b, Feature.AllowUnQuotedFieldNames);
        return new ArcDoc(files, path, json);
    }

    public String getPath(long kk)
    {
        long k1 = kk % Arcturus.K1;
        kk /= Arcturus.K1;
        long k2 = kk % Arcturus.K2;
        kk /= Arcturus.K2;
        long k3 = kk % Arcturus.K3;
        kk /= Arcturus.K3;

        return Common.pathOf(root[(int)k1], name + "." + k1, k2 + "/" + k3 + "/" + kk);
    }

    public Map put(long key, Map value)
    {
        String path = getPath(key);

        Map last = (Map)get(key);

        ArcTool.text.push(path + "/" + content, JSON.toJSONBytes(value));

        if (value != null && index != null) for (Map.Entry<String, Object> e : index.entrySet())
        {
            JSONArray list = (JSONArray)JSON.toJSON(e.getValue());
            for (Object k : list)
            {
                Object val = value.get(k);
                Object old = last == null ? null : last.get(k);

                if (Common.isEmpty(old))
                {
                    if (!Common.isEmpty(val))
                        addSeek(key, e.getKey(), val);
                }
                else
                {
                    if (!old.equals(val))
                    {
                        addSeek(-key, e.getKey(), old); //remove

                        if (!Common.isEmpty(val))
                            addSeek(key, e.getKey(), val);
                    }
                }
            }
        }

        return last;
    }

    private void addSeek(Long key, String index, Object val)
    {
        String valStr = val.toString();
        String valPath = getPath(ArcTool.getSign(valStr));
        ArcTool.seek.push(valPath + "/" + index + "." + valStr, key);
    }

    public Map remove(long key)
    {
        throw new RuntimeException("not support");
    }
}
