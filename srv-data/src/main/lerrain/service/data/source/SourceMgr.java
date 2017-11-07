package lerrain.service.data.source;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lerrain.service.data.source.arcturus.ArcMap;
import lerrain.service.data.source.arcturus.ArcTool;
import lerrain.service.data.source.arcturus.Arcturus;
import lerrain.service.data.source.db.DataBase;
import lerrain.tool.Common;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class SourceMgr
{
    public static final int TYPE_DB         = 1;
    public static final int TYPE_ARC        = 2;

    Map<Long, Object> map = new HashMap<>();

    public SourceMgr()
    {
        ArcTool.start();
    }

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
            JSONObject js = (JSONObject)JSON.toJSON(opts);

            String code = js.getString("code");
            String content = js.getString("content");

            String[] arcPath = js.getJSONArray("path").toArray(new String[(int) Arcturus.K1]);
            Map<String, Object> files = js.getJSONObject("files");
            Map<String, Object> index = js.getJSONObject("index");

            ArcMap arcMap = new ArcMap(arcPath, code, content, files, index);
            map.put(sourceId, arcMap);
        }
    }

    public Object getSource(Long sourceId)
    {
        return map.get(sourceId);
    }

    public Object getSource(String code)
    {
        return map.get(code);
    }
}
