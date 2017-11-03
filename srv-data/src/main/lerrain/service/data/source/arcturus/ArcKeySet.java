package lerrain.service.data.source.arcturus;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by lerrain on 2017/9/27.
 */
public class ArcKeySet implements Set<Long>
{
    ArcMap lsm;

    public ArcKeySet(ArcMap lsm)
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
    public Iterator<Long> iterator()
    {
        return new ArcKeyIterator(lsm);
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
    public boolean add(Long aLong)
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
    public boolean addAll(Collection<? extends Long> c)
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

    }
}
