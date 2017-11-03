package lerrain.service.data.source.arcturus;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.ParserConfig;
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
    ArcMap arc;

    String path;

    public ArcDoc(ArcMap arc, String path, Map map)
    {
        this.arc = arc;
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
            String str = f.exists() ? Disk.load(f, "utf-8") : null;

            super.put(s, str);
            return str;
        }

        return super.get(s);
    }
}
