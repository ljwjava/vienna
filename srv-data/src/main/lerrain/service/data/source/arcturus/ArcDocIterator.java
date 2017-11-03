package lerrain.service.data.source.arcturus;

import com.alibaba.fastjson.JSON;
import lerrain.tool.Common;
import lerrain.tool.Disk;

import java.io.File;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by lerrain on 2017/9/27.
 */
public class ArcDocIterator implements Iterator<Map>
{
    ArcIterator arc;

    public ArcDocIterator(ArcMap lsm)
    {
        arc = new ArcIterator(lsm);
    }

    @Override
    public boolean hasNext()
    {
        return arc.hasNext();
    }

    @Override
    public Map next()
    {
        arc.next();
        return arc.getValue();
    }
}
