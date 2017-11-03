package lerrain.service.data.source.arcturus;

import lerrain.tool.Common;

import java.io.File;
import java.util.Iterator;

/**
 * Created by lerrain on 2017/9/27.
 */
public class ArcKeyIterator implements Iterator<Long>
{
    ArcIterator arc;

    public ArcKeyIterator(ArcMap lsm)
    {
        arc = new ArcIterator(lsm);
    }

    @Override
    public boolean hasNext()
    {
        return arc.hasNext();
    }

    @Override
    public Long next()
    {
        arc.next();
        return arc.getKey();
    }
}
