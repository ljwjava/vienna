package lerrain.service.data.source;

import lerrain.service.data.source.arcturus.ArcMap;
import lerrain.service.data.source.db.DataBase;
import lerrain.tool.Common;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class SourceMgr
{
    public static final int TYPE_DB         = 1;
    public static final int TYPE_ARC        = 2;

    @Value("${path.arcturus}")
    String arcturusPath;

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
            String arcPath = Common.isEmpty(arcturusPath) ? (String)opts.get("path") : arcturusPath;
            ArcMap arcMap = new ArcMap(arcPath, (String)opts.get("name"));

            map.put(sourceId, arcMap);
        }
    }

    public Object getSource(Long sourceId)
    {
        return map.get(sourceId);
    }
}
