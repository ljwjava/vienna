package lerrain.service.dict.ip;

import com.sun.org.apache.xpath.internal.FoundIndex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class Importer
{
    @Autowired
    JdbcTemplate jdbc;

    int reign = 256;
    int cluster = 256;

    long c2 = 0;

    String path = "f:/2/2/";

//    @PostConstruct
    public void start() throws Exception
    {
        long size = jdbc.queryForObject("select count(*) from dict_ip_cn", Long.class);

        DataOutputStream fis = null, fos;

        long k = 0, l = 0;
        String p1 = null;

        List<String> files = new ArrayList<>();
        List<long[]> split = new ArrayList<>();

        String sql = "select * from dict_ip_cn where start_num > ? order by start_num limit 0, ?";

        for (long i=0,j=0;i<size;i=i+cluster,j++)
        {
            if (j % reign == 0)
            {
                if (!files.isEmpty())
                    block(k, split, files);

                k = j / reign;

                p1 = path + k + "/";
                new File(p1).mkdirs();
            }

//            if (j < 256)
//                continue;

            files.add(p1 + j);
            fis = new DataOutputStream(new FileOutputStream(new File(p1 + j + ".map")));
            fos = new DataOutputStream(new FileOutputStream(new File(p1 + j + ".cluster")));

            System.out.println(i + ", " + size);

            RowMapper rm = new RowMapper(fis, fos);
            jdbc.query(sql, rm, l, cluster);
            split.add(rm.getRange());

            l = rm.getRange()[1];

            fis.close();
            fos.close();
        }

        if (!files.isEmpty())
            block(k, split, files);
    }

    private void block(long n, List<long[]> lp, List<String> pp) throws Exception
    {
        DataOutputStream fos = new DataOutputStream(new FileOutputStream(new File(path + n + ".dip")));

        for (int i=0;i<reign;i++)
        {
            long[] p = i < lp.size() ? lp.get(i) : new long[] {0, 0};
            fos.writeInt((int) p[0]);
            fos.writeInt((int)p[1]);
        }

        for (String p : pp)
        {
            writeToStream(fos, new File(p + ".map"));
            writeToStream(fos, new File(p + ".cluster"));
        }

        fos.close();

        lp.clear();
        pp.clear();
    }

    private void writeToStream(OutputStream os, File f) throws Exception
    {
        byte[] b = new byte[1024];
        FileInputStream f1 = new FileInputStream(f);
        int c = -1;
        while ((c = f1.read(b)) > 0)
        {
            os.write(b, 0, c);
        }
        f1.close();
    }

    private byte[] byteOf(String s1, String s2, String s3)
    {
        int i = 1;
        byte[] b = new byte[128];

        if (s1 != null)
        {
            byte[] c = s1.getBytes();
            for (byte bb : c)
            {
                b[i++] = bb;
            }
        }
        if (s2 != null)
        {
            byte[] c = s2.getBytes();
            for (byte bb : c)
            {
                b[i++] = bb;
            }
        }
        if (s3 != null)
        {
            byte[] c = s3.getBytes();
            for (byte bb : c)
            {
                b[i++] = bb;
            }
        }
        b[0] = (byte)i;

        return b;
    }

    private class RowMapper implements RowCallbackHandler
    {
        DataOutputStream fis;
        DataOutputStream fos;

        long m = -1, n = -1;

        public RowMapper(DataOutputStream fis, DataOutputStream fos)
        {
            this.fis = fis;
            this.fos = fos;
        }

        public long[] getRange()
        {
            return new long[] {m, n};
        }

        @Override
        public void processRow(ResultSet rs) throws SQLException
        {
            long x = rs.getLong("start_num");
            long y = rs.getLong("end_num");

            if (x <= c2)
                System.out.println(String.format("%d, %d, %d", c2, x, y));

            if (c2 < y)
                c2 = y;

            if (m < 0 || m > x)
                m = x;
            if (n < 0 || n < y)
                n = y;

            String s1 = rs.getString("prov");
            String s2 = rs.getString("city");
            String s3 = rs.getString("dist");

            double lgt = rs.getDouble("longitude");
            double lat = rs.getDouble("latitude");

            try
            {
                fis.writeInt((int)x);
                fis.writeInt((int)y);

                fos.write(byteOf(s1, s2, s3));
                fos.writeDouble(lgt);
                fos.writeDouble(lat);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}

