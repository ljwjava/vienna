package lerrain.service.dict.ip;

import com.alibaba.fastjson.JSONObject;
import lerrain.tool.Common;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;

/**
 * Created by lerrain on 2017/7/26.
 */
@Service
public class DipSeeker
{
    @Value("${path.ip}")
    String path;

    int reign = 256;
    int cluster = 256;

    long MAX = 256L * 256 * 256 * 256;

    long[][] seek = new long[512][2];
    File[] dips = new File[512];
    int size = 0;

    public DipSeeker()
    {
    }

    @PostConstruct
    public void reset()
    {
        if (Common.isEmpty(path))
            return;

        int s = 0;

        File ff = new File(path);
        for (File f : ff.listFiles())
        {
            if (!f.getName().endsWith(".dip"))
                continue;

            try (DataInputStream dis = new DataInputStream(new FileInputStream(f)))
            {
                seek[s][0] = dis.readInt();
                dis.skipBytes(4 * 255 * 2);
                seek[s][1] = dis.readInt();

                if (seek[s][0] < 0)
                    seek[s][0] += MAX;
                if (seek[s][1] < 0)
                    seek[s][1] += MAX;

                dips[s] = f;

                s++;
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        size = s;
    }

    public JSONObject find(String ip)
    {
        String[] s = ip.split("[.]");
        return find((Long.parseLong(s[0]) << 24) + (Long.parseLong(s[1]) << 16) + (Long.parseLong(s[2]) << 8) + (Long.parseLong(s[3])));
    }

    public JSONObject find(long ip)
    {
        for (int i=0;i<size;i++)
        {
            if (seek[i][0] <= ip && seek[i][1] >= ip)
            {
                try (DataInputStream dis = new DataInputStream(new FileInputStream(dips[i])))
                {
                    for (int j=0;j<reign;j++)
                    {
                        long p1 = dis.readInt();
                        long p2 = dis.readInt();

                        if (p1 < 0) p1 += MAX;
                        if (p2 < 0) p2 += MAX;

                        if (p1 <= ip && p2 >= ip)
                        {
                            dis.skipBytes((reign - j - 1) * 8 + (36864 + 2048) * j);
                            for (int k=0;k<cluster;k++)
                            {
                                long k1 = dis.readInt();
                                long k2 = dis.readInt();

                                if (k1 < 0) k1 += MAX;
                                if (k2 < 0) k2 += MAX;

                                if (k1 <= ip && k2 >= ip)
                                {
                                    dis.skipBytes((cluster - k - 1) * 8 + (128 + 16) * k);

                                    byte l = dis.readByte();
                                    byte[] b = new byte[127];
                                    dis.read(b, 0, 127);

                                    double lgt = dis.readDouble();
                                    double lat = dis.readDouble();

//                                    System.out.println(k1 + ", " + k2);
//                                    System.out.println(new String(b, 0, l));
//                                    System.out.println(lgt);
//                                    System.out.println(lat);

                                    JSONObject r = new JSONObject();
                                    r.put("longitude", lgt);
                                    r.put("latitude", lat);
                                    r.put("address", new String(b, 0, l));

                                    return r;
                                }
                            }

                            return null;
                        }
                    }

                    return null;
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

//    public static void main(String[] s)
//    {
//        String[] str = new String[] {"58.247.39.157", "139.226.39.157", "158.247.39.157"};
//
//        DipSeeker ds = new DipSeeker();
//
//        for (String ip : str)
//        {
//            long t1 = System.currentTimeMillis();
//            ds.find(ip);
//            System.out.println(System.currentTimeMillis() - t1 + "ms");
//        }
//    }
}
