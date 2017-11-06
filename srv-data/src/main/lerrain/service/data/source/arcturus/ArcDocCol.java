package lerrain.service.data.source.arcturus;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by lerrain on 2017/9/27.
 */
public class ArcDocCol implements Collection<Map>
{
    ArcMap lsm;

    public ArcDocCol(ArcMap lsm)
    {
        this.lsm = lsm;
    }

    @Override
    public int size()
    {
        return 0;
    }

    @Override
    public boolean isEmpty()
    {
        return false;
    }

    @Override
    public boolean contains(Object o)
    {
        return false;
    }

    @Override
    public Iterator<Map> iterator()
    {
        return new ArcDocIterator(lsm);
    }

    @Override
    public Object[] toArray()
    {
        return null;
    }

    @Override
    public <T> T[] toArray(T[] a)
    {
        return null;
    }

    @Override
    public boolean add(Map doc)
    {
        return false;
    }

    @Override
    public boolean remove(Object o)
    {
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c)
    {
        return false;
    }

    @Override
    public boolean addAll(Collection<? extends Map> c)
    {
        return false;
    }

    @Override
    public boolean removeAll(Collection<?> c)
    {
        return false;
    }

    @Override
    public boolean retainAll(Collection<?> c)
    {
        return false;
    }

    @Override
    public void clear()
    {
        throw new RuntimeException("not support");
    }
}
