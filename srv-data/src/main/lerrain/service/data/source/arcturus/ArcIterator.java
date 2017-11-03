package lerrain.service.data.source.arcturus;

import com.alibaba.fastjson.JSON;
import lerrain.tool.Common;
import lerrain.tool.Disk;

import java.io.File;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by lerrain on 2017/9/27.
 */
public class ArcIterator
{
    ArcMap lsm;

    File[] dir1;
    File[] dir2;
    File[] dir3;
    File[] valf;

    int k1;
    int k2;
    int k3;
    int k4;

    public ArcIterator(ArcMap lsm)
    {
        this.lsm = lsm;

        synchronized (lsm)
        {
            dir1 = new File(Common.pathOf(lsm.root, lsm.name)).listFiles();
            k1 = 0;

            if (dir1.length > k1)
                dir2 = dir1[k1].listFiles();
            k2 = 0;

            if (dir2 != null && dir2.length > k2)
                dir3 = dir2[k2].listFiles();
            k3 = 0;

            if (dir3 != null && dir3.length > k3)
                valf = dir3[k3].listFiles();
            k4 = -1;
        }
    }

    public boolean hasNext()
    {
        synchronized (lsm)
        {
            if (valf == null)
                return false;

            if (valf.length > k4 + 1)
                return true;

            if (dir3.length > k3 + 1)
                return true;

            if (dir2.length > k2 + 1)
                return true;

            if (dir1.length > k1 + 1)
                return true;
        }

        return false;
    }

    public void next()
    {
        synchronized (lsm)
        {
            k4++;

            if (valf.length <= k4)
            {
                k4 = 0;
                k3++;
            }

            if (dir3.length <= k3)
            {
                k3 = 0;
                k2++;
            }

            if (dir2.length <= k2)
            {
                k2 = 0;
                k1++;
            }

            if (dir1.length <= k1)
                throw new RuntimeException("already finish");

            if (k2 == 0)
                dir2 = dir1[k1].listFiles();

            if (k3 == 0)
                dir3 = dir2[k2].listFiles();

            if (k4 == 0)
                valf = dir3[k3].listFiles();
        }
    }

    public Long getKey()
    {
        return Integer.parseInt(valf[k4].getName()) * ArcMap.K3 * ArcMap.K2 * ArcMap.K1 + Integer.parseInt(dir3[k3].getName()) * ArcMap.K2 * ArcMap.K1 + Integer.parseInt(dir2[k2].getName()) * ArcMap.K1 + Integer.parseInt(dir1[k1].getName());
//        return Integer.parseInt(valf[k4].getName()) * ArcMap.K2 * ArcMap.K1 + Integer.parseInt(dir2[k2].getName()) * ArcMap.K1 + Integer.parseInt(dir1[k1].getName());
    }

    public Map getValue()
    {
        synchronized (lsm)
        {
            return JSON.parseObject(Disk.load(new File(Common.pathOf(valf[k4].getAbsolutePath(), lsm.primary)), "utf-8"));
        }
    }
}
