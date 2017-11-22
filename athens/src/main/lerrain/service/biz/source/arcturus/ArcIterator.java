package lerrain.service.biz.source.arcturus;

import com.alibaba.fastjson.JSON;
import lerrain.tool.Common;
import lerrain.tool.Disk;

import java.io.File;
import java.util.Map;

/**
 * Created by lerrain on 2017/9/27.
 */
public class ArcIterator
{
    Arcturus lsm;

    File[] dir1;
    File[] dir2;
    File[] dir3;
    File[] valf;

    int k1;
    int k2;
    int k3;
    int k4;

    Long key;
    File file;

    public ArcIterator(Arcturus lsm)
    {
        this.lsm = lsm;

        synchronized (ArcTool.text.pack)
        {
            k1 = 0;
            dir1 = new File[(int)Arcturus.K1];
            for (int i=0;i<Arcturus.K1;i++)
                dir1[i] = new File(Common.pathOf(lsm.root[i], lsm.name + "." + i));

            k2 = 0;
            if (dir1.length > k1)
            {
                dir2 = dir1[k1].listFiles();

                while (dir2 == null || dir2.length == 0)
                {
                    k1++;

                    if (dir1.length <= k1)
                        throw new RuntimeException("eof exception");

                    dir2 = dir1[k1].listFiles();
                }
            }

            k3 = 0;
            if (dir2 != null && dir2.length > k2)
                dir3 = dir2[k2].listFiles();

            k4 = -1;
            if (dir3 != null && dir3.length > k3)
                valf = dir3[k3].listFiles();
        }
    }

    private void scan()
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
            throw new RuntimeException("eof exception");

        if (k2 == 0) //第一层目录一直存在，但内容可能是全空的
        {
            dir2 = dir1[k1].listFiles();

            while (dir2 == null || dir2.length == 0)
            {
                k1++;

                if (dir1.length <= k1)
                    throw new RuntimeException("eof exception");

                dir2 = dir1[k1].listFiles();
            }
        }

        if (k3 == 0)
        {
            dir3 = dir2[k2].listFiles();
        }

        if (k4 == 0)
        {
            valf = dir3[k3].listFiles();
        }
    }

    public boolean hasNext()
    {
        synchronized (ArcTool.text.pack)
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
            {
                int w = 1;
                File[] dir = dir1[k1 + w].listFiles();

                while (dir == null || dir.length == 0)
                {
                    w++;

                    if (dir1.length <= k1 + w)
                        return false;

                    dir = dir1[k1 + w].listFiles();
                }

                return true;
            }
        }

        return false;
    }

    public void next()
    {
        synchronized (ArcTool.text.pack)
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
                throw new RuntimeException("eof exception");

            if (k2 == 0) //第一层目录一直存在，但内容可能是全空的
            {
                dir2 = dir1[k1].listFiles();

                while (dir2 == null || dir2.length == 0)
                {
                    k1++;

                    if (dir1.length <= k1)
                        throw new RuntimeException("eof exception");

                    dir2 = dir1[k1].listFiles();
                }
            }

            if (k3 == 0)
            {
                dir3 = dir2[k2].listFiles();
            }

            if (k4 == 0)
            {
                valf = dir3[k3].listFiles();
            }
        }
    }

    public Long getKey()
    {
        return Integer.parseInt(valf[k4].getName()) * Arcturus.K3 * Arcturus.K2 * Arcturus.K1 + Integer.parseInt(dir3[k3].getName()) * Arcturus.K2 * Arcturus.K1 + Integer.parseInt(dir2[k2].getName()) * Arcturus.K1 + k1;
    }

    public Map getValue()
    {
        synchronized (ArcTool.text.pack)
        {
            return JSON.parseObject(Disk.load(new File(Common.pathOf(valf[k4].getAbsolutePath(), lsm.content)), "utf-8"));
        }
    }
}
