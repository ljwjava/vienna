package lerrain.service.data.source.arcturus;

import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    public static List find(ArcMap arcMap, String indexName, String valStr)
    {
        List<Map> r = new ArrayList();

        Long valSign = ArcTool.getSign(valStr);

        String path = arcMap.getPath(valSign) + "/" + indexName + "." + valStr;

        try (RandomAccessFile fos = new RandomAccessFile(path, "r"))
        {
            long len = fos.length();
            for (int i = 0; i < len; i += 8)
            {
                r.add(arcMap.get(fos.readLong()));
            }
        }
        catch (Exception e2)
        {
            throw new RuntimeException(e2);
        }

        return r;
    }

    public static String save(ArcDoc doc, String key, String value)
    {
        return null;
    }
}
