package lerrain.service.biz.source.arcturus;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by lerrain on 2017/9/27.
 */
public class ArcEntrySet implements Set<Map.Entry<Long, Map>>
{
    Arcturus lsm;

    public ArcEntrySet(Arcturus lsm)
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
    public Iterator<Map.Entry<Long, Map>> iterator()
    {
        return new ArcEntryIterator(lsm);
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
    public boolean add(Map.Entry<Long, Map> aLong)
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
    public boolean addAll(Collection<? extends Map.Entry<Long, Map>> c)
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
