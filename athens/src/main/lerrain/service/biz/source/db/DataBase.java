package lerrain.service.biz.source.db;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lerrain.service.common.Log;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lerrain on 2017/9/26.
 */
public class DataBase extends HashMap
{
    DruidDataSource dds;

    Map map;

    public DataBase(Map opts)
    {
        map = new HashMap();
        map.put("driverClassName", "com.mysql.jdbc.Driver");
        map.put("initialSize", "1");
        map.put("minIdle", "1");
        map.put("maxActive", "20");
        map.put("maxWait", "60000");
        map.put("timeBetweenEvictionRunsMillis", "60000");
        map.put("minEvictableIdleTimeMillis", "300000");
        map.put("validationQuery", "SELECT '1'");
        map.put("testWhileIdle", "true");
        map.put("testOnBorrow", "false");
        map.put("testOnReturn", "false");
        map.put("poolPreparedStatements", "false");
        map.put("removeAbandoned", "true");
        map.put("removeAbandonedTimeout", "1800");
        map.put("logAbandoned", "true");
        map.putAll(opts);

        this.put("query", new DbQuery(this));
        this.put("find", new DbFind(this));
        this.put("save", new DbSave(this));
        this.put("replace", new DbReplace(this));
        this.put("column", new DbColumn(this));
    }

    public void initiate()
    {
        try
        {
            dds = (DruidDataSource) DruidDataSourceFactory.createDataSource(map);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public void disconnect()
    {
        dds.close();
    }

    public Map getColumnType(String sql, Object[] vals)
    {
        try (DruidPooledConnection dpc = dds.getConnection(); PreparedStatement ps = dpc.prepareStatement(sql);)
        {
            if (vals != null)
                for (int i=0;i<vals.length;i++)
                    ps.setObject(i+1, vals[i]);

            try (ResultSet rs = ps.executeQuery())
            {
                JSONObject v = new JSONObject();

                ResultSetMetaData rsmd = rs.getMetaData();
                int num = rsmd.getColumnCount();
                for (int i = 1; i <= num; i++)
                    v.put(rsmd.getColumnLabel(i), rsmd.getColumnTypeName(i));

                return v;
            }
        }
        catch (Exception e)
        {
            Log.error(e);
        }

        return null;
    }

    public Map queryMap(String sql, Object[] vals, Map<String, String> mapping)
    {
        try (DruidPooledConnection dpc = dds.getConnection(); PreparedStatement ps = dpc.prepareStatement(sql);)
        {
            if (vals != null)
                for (int i=0;i<vals.length;i++)
                    ps.setObject(i+1, vals[i]);

            try (ResultSet rs = ps.executeQuery())
            {
                if (rs.first())
                {
                    JSONObject v = new JSONObject();

                    if (mapping == null)
                    {
                        ResultSetMetaData rsmd = rs.getMetaData();

                        int num = rsmd.getColumnCount();
                        for (int i = 1; i <= num; i++)
                        {
                            String key = rsmd.getColumnLabel(i);
                            Object val = rs.getObject(i);

                            v.put(key, val);
                        }
                    }
                    else
                    {
                        for (Map.Entry<String, String> e : mapping.entrySet())
                            v.put(e.getValue(), rs.getObject(e.getKey()));
                    }

                    return v;
                }
            }
        }
        catch (Exception e)
        {
            Log.error(e);
        }

        return null;
    }

    public void update(String sql, Object[] vals)
    {
        Log.info(sql);

        try (DruidPooledConnection dpc = dds.getConnection(); PreparedStatement ps = dpc.prepareStatement(sql);)
        {
            if (vals != null)
                for (int i=0;i<vals.length;i++)
                    ps.setObject(i+1, vals[i]);

            ps.execute();
        }
        catch (Exception e)
        {
            Log.error(e);
        }
    }

    public boolean updateAll(String sql, List<Object[]> list)
    {
        Log.info(sql + " <= " + list.size() + " rows");

        try (DruidPooledConnection dpc = dds.getConnection(); PreparedStatement ps = dpc.prepareStatement(sql);)
        {
            boolean r = true;

            for (Object[] vals : list)
            {
                if (vals != null)
                    for (int i = 0; i < vals.length; i++)
                        ps.setObject(i + 1, vals[i]);

                r = ps.execute() && r;
            }

            return r;
        }
        catch (Exception e)
        {
            Log.error(e);
        }

        return false;
    }

//    public Map getCols(String table)
//    {
//        try (DruidPooledConnection dpc = dds.getConnection())
//        {
//            PreparedStatement ps = dpc.prepareStatement("select * from " + table + " limit 0, 0");
//
//            ResultSet rs = ps.executeQuery();
//            ResultSetMetaData rsmd = rs.getMetaData();
//
//            Map<String, String> map = new HashMap<>();
//
//            int num = rsmd.getColumnCount();
//            for (int i = 1; i <= num; i++)
//            {
//                String key = rsmd.getColumnLabel(i);
//                map.put(key, rsmd.getColumnTypeName(i));
//            }
//
//            return map;
//        }
//        catch (Exception e)
//        {
//            Log.error(e);
//        }
//
//        return null;
//    }

    public boolean queryBool(String sql, Object[] vals)
    {
        try (DruidPooledConnection dpc = dds.getConnection(); PreparedStatement ps = dpc.prepareStatement(sql);)
        {
            if (vals != null)
                for (int i=0;i<vals.length;i++)
                    ps.setObject(i+1, vals[i]);

            try (ResultSet rs = ps.executeQuery())
            {
                if (rs.first())
                    return rs.getBoolean(1);
            }
        }
        catch (Exception e)
        {
            Log.error(e);
        }

        return false;
    }

    public List queryList(String sql, Object[] vals, Map<String, String> mapping)
    {
        try (DruidPooledConnection dpc = dds.getConnection(); PreparedStatement ps = dpc.prepareStatement(sql);)
        {
            if (vals != null)
                for (int i=0;i<vals.length;i++)
                    ps.setObject(i+1, vals[i]);

            JSONArray r = new JSONArray();

            try (ResultSet rs = ps.executeQuery())
            {
                ResultSetMetaData rsmd = rs.getMetaData();
                int num = rsmd.getColumnCount();

                while (rs.next())
                {
                    JSONObject v = new JSONObject();

                    if (mapping == null)
                    {
                        for (int i = 1; i <= num; i++)
                        {
                            String key = rsmd.getColumnLabel(i);
                            Object val = rs.getObject(i);

                            v.put(key, val);
                        }
                    }
                    else if (mapping.isEmpty())
                    {
                        for (int i = 1; i <= num; i++)
                        {
                            String key = rsmd.getColumnLabel(i);
                            Object val = rs.getObject(i);

                            int pos = key.indexOf("_");
                            while (pos >= 0)
                            {
                                try
                                {
                                    key = key.substring(0, pos) + key.substring(pos + 1, pos + 2).toUpperCase() + key.substring(pos + 2);
                                }
                                catch (Exception e)
                                {
                                    break;
                                }

                                pos = key.indexOf("_");
                            }

                            v.put(key, val);
                        }
                    }
                    else
                    {
                        for (Map.Entry<String, String> e : mapping.entrySet())
                            v.put(e.getValue(), rs.getObject(e.getKey()));
                    }

                    r.add(v);
                }
            }

            return r;
        }
        catch (Exception e)
        {
            Log.error(e);
        }

        return null;
    }
}
