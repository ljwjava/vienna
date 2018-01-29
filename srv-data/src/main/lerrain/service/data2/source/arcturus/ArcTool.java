package lerrain.service.data2.source.arcturus;

import java.io.*;
import java.util.*;

/**
 * Created by lerrain on 2017/11/3.
 */
public class ArcTool
{
    static ArcTextQueue text = new ArcTextQueue();
    static ArcSeekQueue seek = new ArcSeekQueue();

    public static void start()
    {
        text.start();
        seek.start();
    }

    public static void stop()
    {
        text.stop();
        seek.stop();
    }

    public static void addIndex(ArcMap arcMap, String indexName)
    {

    }

    public static Long getSign(String str)
    {
        long key = (long)str.hashCode();
        return key >= 0 ? key : Integer.MAX_VALUE - key;
    }

    public static List find(Arcturus arc, String indexName, String valStr)
    {
        Set<Long> temp = new java.util.HashSet<>();

        Long valSign = ArcTool.getSign(valStr);

        String path = arc.getPath(valSign) + "/" + indexName + "." + valStr;

        try (DataInputStream dis = new DataInputStream(new FileInputStream(path)))
        {
            long id = dis.readLong();
            if (id > 0)
                temp.add(id);
            else
                temp.remove((Long) (-id));
        }
        catch (EOFException e1)
        {
        }
        catch (IOException e2)
        {
            throw new RuntimeException(e2);
        }

        List r = new ArrayList();
        for (Long id : temp)
            r.add(arc.get(id));

        return r;
    }

    public static String save(ArcDoc doc, String key, String value)
    {
        return null;
    }
}
