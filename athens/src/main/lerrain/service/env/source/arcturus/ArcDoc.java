package lerrain.service.env.source.arcturus;

import com.alibaba.fastjson.JSON;
import lerrain.tool.Common;
import lerrain.tool.Disk;
import lerrain.tool.formula.Factors;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lerrain on 2017/11/2.
 */
public class ArcDoc extends HashMap<String, Object> implements Factors
{
    Map files;

    String path;

    public ArcDoc(Map files, String path, Map map)
    {
        this.files = files;
        this.path = path;

        if (map != null)
            putAll(map);
    }

    @Override
    public Object get(String s)
    {
        if (!super.containsKey(s))
        {
            File f = new File(Common.pathOf(path, s));
            Object res = f.exists() ? Disk.load(f, "utf-8") : null;

            int type = Common.intOf(files.get(s), 0);
            if (type == 2)
                res = JSON.parseObject((String)res);

            super.put(s, res);
            return res;
        }

        return super.get(s);
    }
}
