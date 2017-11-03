package lerrain.service.data;

import com.alibaba.fastjson.JSONObject;
import lerrain.service.data.source.arcturus.ArcMap;

import java.util.Random;

/**
 * Created by lerrain on 2017/11/2.
 */
public class Test
{
    public static void main(String[] s)
    {
        ArcMap arc = new ArcMap("x:/arcturus/", "user");

        long t1 = System.currentTimeMillis();
        long t2 = System.currentTimeMillis();
        int q1 = 200000, q2 = 100000;

//        for (long i=0;i<q1;i++)
//        {
//            JSONObject json = new JSONObject();
//            json.put("id", i);
//            json.put("parentId", 0);
//            json.put("name", "王老"+i);
//            json.put("mobile", String.format("131%08d", i));
//            json.put("certNo", String.format("4107%014d", i));
//            json.put("test", "123123/"+i);
//
//            arc.put(i, json);
//
//            if (i % 1000 == 0)
//                System.out.println(i * 100.0f / q1 + "%");
//
//            if (i % 10000 == 0)
//            {
//                System.out.println((System.currentTimeMillis() - t2) + "ms per 10000 record");
//                t2 = System.currentTimeMillis();
//            }
//        }

        System.out.println((System.currentTimeMillis() - t1) / 1.0f / q1 + "ms per record");

        t1 = System.currentTimeMillis();

        Random ran = new Random();
        for (long i=0;i<q2;i++)
        {
            System.out.println(arc.get((long) ran.nextInt(200000)));
        }

        System.out.println((System.currentTimeMillis() - t1) / 1.0f / q2 + "ms per record");
    }
}
