package lerrain.service.env.source.arcturus;

import java.util.Iterator;

/**
 * Created by lerrain on 2017/9/27.
 */
public class ArcKeyIterator implements Iterator<Object>
{
    ArcIterator arc;

    public ArcKeyIterator(Arcturus lsm)
    {
        arc = new ArcIterator(lsm);
    }

    @Override
    public boolean hasNext()
    {
        return arc.hasNext();
    }

    @Override
    public Object next()
    {
        arc.next();
        return arc.getKey();
    }

    @Override
    public void remove()
    {
        throw new RuntimeException("not support");
    }
}
