package lerrain.service.biz.source.arcturus;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lerrain.tool.Disk;

import java.io.File;
import java.util.Random;

;

/**
 * Created by lerrain on 2017/11/2.
 */
public class Test implements Runnable
{
    static int q1 = 100;
    static int times = 10;

    static Random ran = new Random();

    public static void main(String[] s) throws Exception
    {

        long tt = System.currentTimeMillis();

        ArcMap arc = new ArcMap(new String[] { "d:/arcturus/", "d:/arcturus/", "d:/arcturus/", "d:/arcturus/" }, "biz");

        ArcTool.start();
        int t = times;
        for (int i=0;i<t;i++)
            new Thread(new Test(arc, i)).start();

        for (int i=0;i<3600;i++)
        {
            Thread.sleep(1000);

            int len1 = ArcTool.text.getQueueSize();
            int len2 = ArcTool.seek.getQueueSize();
            System.out.println(len1 + ", " + len2);

            if (len1 <= 0 && len2 <= 0)
                break;
        }

        System.out.println("COST: " + (System.currentTimeMillis() - tt) / 1000 + "s");

//          int q2 = 100000;
//        long t1 = System.currentTimeMillis();
//
//        for (long i=0;i<q2;i++)
//        {
//            arc.get((long) ran.nextInt(q1 * times));
//
//            if (i % 1000 == 0)
//                System.out.println(i);
//        }
//


//        long t1 = System.currentTimeMillis();
//        for (long i=0;i<q2;i++)
//        {
//            ArcTool.find(arc.arc, "mobile", String.format("131%08d", i));
//        }
//
//        System.out.println((System.currentTimeMillis() - t1) / 1.0f / q2 + "ms per record");
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
            json.put("mobile", String.format("131%08d", j * q1 + i));

            arc.put(j * q1 + i + 1, json);
        }

        System.out.println((System.currentTimeMillis() - t2) / 1.0f / q1 + "ms per record");

        times--;
        if (times == 0)
            ArcTool.stop();
    }
}
