package lerrain.service.data.source.arcturus;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.ParserConfig;
import lerrain.tool.Common;
import lerrain.tool.Disk;
import lerrain.tool.formula.Factors;
import lerrain.tool.formula.Function;

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
            Object res = f.exists() ? Disk.load(f, "utf-8") : null;

            Integer type = arc.files.getInteger(s);
            if (type != null && type == 2)
                res = JSON.parseObject((String)res);

            super.put(s, res);
            return res;
        }

        return super.get(s);
    }
}
