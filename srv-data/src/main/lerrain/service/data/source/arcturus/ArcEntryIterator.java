package lerrain.service.data.source.arcturus;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lerrain.tool.Common;
import lerrain.tool.Disk;

import java.io.File;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by lerrain on 2017/9/27.
 */
public class ArcEntryIterator implements Iterator<Map.Entry<Long, Map>>
{
    ArcIterator arc;

    public ArcEntryIterator(Arcturus lsm)
    {
        arc = new ArcIterator(lsm);
    }

    @Override
    public boolean hasNext()
    {
        return arc.hasNext();
    }

    @Override
    public Map.Entry<Long, Map> next()
    {
        arc.next();
        return new Entry(arc.getKey(), arc.getValue());
    }

    @Override
    public void remove()
    {
        throw new RuntimeException("not support");
    }

    private class Entry implements Map.Entry<Long, Map>
    {
        Long key;
        Map val;

        public Entry(Long key, Map val)
        {
            this.key = key;
            this.val = val;
        }

        @Override
        public Long getKey()
        {
            return key;
        }

        @Override
        public Map getValue()
        {
            return val;
        }

        @Override
        public Map setValue(Map value)
        {
            return val;
        }
    }
}
