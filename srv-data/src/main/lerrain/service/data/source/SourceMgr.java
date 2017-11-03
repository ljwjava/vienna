package lerrain.service.data.source;

import lerrain.service.data.source.arcturus.ArcMap;
import lerrain.service.data.source.db.DataBase;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class SourceMgr
{
    public static final int TYPE_DB         = 1;
    public static final int TYPE_ARC        = 2;

    Map<Long, Object> map = new HashMap<>();

    public void add(Long sourceId, int type, Map opts)
    {
        if (type == TYPE_DB)
        {
            DataBase db = new DataBase(opts);
            db.initiate();

            map.put(sourceId, db);
        }
        else if (type == TYPE_ARC)
        {
            ArcMap arcMap = new ArcMap((String)opts.get("path"), (String)opts.get("name"));

            map.put(sourceId, arcMap);
        }
    }

    public Object getSource(Long sourceId)
    {
        return map.get(sourceId);
    }
}
