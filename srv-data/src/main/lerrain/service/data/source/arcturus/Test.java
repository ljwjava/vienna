package lerrain.service.data.source.arcturus;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lerrain.tool.Disk;

import java.io.File;
import java.util.Random;

/**
 * Created by lerrain on 2017/11/2.
 */
public class Test implements Runnable
{
    static int q1 = 10000;
    static int times = 10;

    public static void main(String[] s) throws Exception
    {
        ArcTool.start();

        ArcMap arc = new ArcMap("x:/arcturus/", "biz");

//        int t = times;
//        for (int i=0;i<t;i++)
//            new Thread(new Test(arc, i)).start();

        long t1 = System.currentTimeMillis();
        int q2 = 100000;

        Random ran = new Random();
        for (long i=0;i<q2;i++)
        {
            arc.get((long) ran.nextInt(q1 * times));

            if (i % 1000 == 0)
                System.out.println(i);
        }

        System.out.println((System.currentTimeMillis() - t1) / 1.0f / q2 + "ms per record");

//        for (int i=0;i<100;i++)
//        {
//            Thread.sleep(5000);
//
//            int len1 = ArcTool.text.map.size() + (ArcTool.text.pack.size() - ArcTool.text.temp);
//            int len2 = ArcTool.seek.map.size() + (ArcTool.seek.pack.size() - ArcTool.seek.temp);
//            System.out.println(len1 + ", " + len2);
//
//            if (len1 <= 0 && len2 <= 0)
//                break;
//        }
    }

    int j = 0;

    ArcMap arc;

    public Test(ArcMap arc, int j)
    {
        this.j = j;
        this.arc = arc;
    }

    @Override
    public void run()
    {
        JSONObject json = JSON.parseObject(Disk.load(new File("f:/t1.json"), "gbk"));
        long t2 = System.currentTimeMillis();

        for (long i=0;i<q1;i++)
        {
            json.put("name", "王老"+i+j);
            json.put("mobile", String.format("131%05d%03d", i, j));

            arc.put(j * q1 + i, json);
        }

        System.out.println((System.currentTimeMillis() - t2) / 1.0f / q1 + "ms per record");

        times--;
        if (times == 0)
            ArcTool.stop();
    }
}
