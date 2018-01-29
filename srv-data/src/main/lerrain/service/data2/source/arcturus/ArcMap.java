package lerrain.service.data2.source.arcturus;

import lerrain.tool.Common;
import lerrain.tool.formula.Factors;
import lerrain.tool.formula.Function;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Created by lerrain on 2017/9/27.
 */
public class ArcMap implements Map<Object, Object>
{
    Arcturus arc;

    Function find;

    public ArcMap(String[] root, String name)
    {
        this(root, name, "content", null, null);
    }

    public ArcMap(String[] root, String name, String content, Map files, Map index)
    {
        this.arc = new Arcturus(root, name, content, files, index);

        find = new ArcFind();
    }

    public Arcturus getArcturus()
    {
        return arc;
    }

    @Override
    public int size()
    {
        throw new RuntimeException("not support");
    }

    @Override
    public boolean isEmpty()
    {
        return false;
    }

    @Override
    public boolean containsKey(Object key)
    {
        return arc.has(Common.toLong(key));
    }

    @Override
    public boolean containsValue(Object value)
    {
        throw new RuntimeException("not support");
    }

    @Override
    public Object get(Object key)
    {
        if ("find".equals(key))
            return find;

        return arc.get(Common.toLong(key));
    }

    @Override
    public Object put(Object key, Object value)
    {
        return arc.put(Common.toLong(key), (Map)value);
    }

    @Override
    public Map remove(Object key)
    {
        return arc.remove(Common.toLong(key));
    }

    @Override
    public void putAll(Map<?, ?> m)
    {
        for (Map.Entry<?, ?> e : m.entrySet())
            put(e.getKey(), e.getValue());
    }

    @Override
    public void clear()
    {
        throw new RuntimeException("not support");
    }

    @Override
    public Set<Object> keySet()
    {
        return new ArcKeySet(this.arc);
    }

    @Override
    public Collection<Object> values()
    {
        return new ArcDocCol(this.arc);
    }

    @Override
    public Set<Entry<Object, Object>> entrySet()
    {
        throw new RuntimeException("not support");
    }

    public class ArcFind implements Function
    {
        @Override
        public Object run(Object[] objects, Factors factors)
        {
            return ArcTool.find(arc, objects[0].toString(), objects[1].toString());
        }
    }
}
