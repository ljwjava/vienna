package lerrain.service.biz.source.db;

import lerrain.tool.Common;
import lerrain.tool.formula.Factors;
import lerrain.tool.formula.Function;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class DbReplace implements Function
{
    DataBase db;

    public DbReplace(DataBase db)
    {
        this.db = db;
    }

    @Override
    public Object run(Object[] objects, Factors factors)
    {
        String table = Common.trimStringOf(objects[0]);
        String sql = null;

        List<Map<String, Object>> list = new ArrayList<>();
        if (objects[1] instanceof Map)
        {
            list.add((Map<String, Object>)objects[1]);
        }
        else
        {
            list = (List<Map<String, Object>>)objects[1];
        }

        String[] keys = null;
        List<Object[]> vv = new ArrayList<>();

        for (Map<String, Object> vals : list)
        {
            if (objects.length >= 3) //手工设定每个字段的类型
            {
                Map<String, String> cols = (Map) objects[2];
                for (Map.Entry<String, String> e : cols.entrySet())
                {
                    if ("time".equals(e.getValue().toLowerCase()) || "date".equals(e.getValue().toLowerCase()) || "datetime".equals(e.getValue().toLowerCase()))
                    {
                        String key = e.getKey();
                        Long time = Common.toLong(vals.get(key));
                        vals.put(key, time == null ? null : Common.getString(new Date(time), null));
                    }
                }
            }

            if (sql == null)
            {
                keys = new String[vals.size()];

                int i = 0;
                StringBuffer sb1 = null;
                StringBuffer sb2 = null;

                for (Map.Entry<String, Object> e : vals.entrySet())
                {
                    if (sb1 == null)
                    {
                        sb1 = new StringBuffer();
                        sb2 = new StringBuffer();
                    }
                    else
                    {
                        sb1.append(",");
                        sb2.append(",");
                    }

                    sb1.append(e.getKey());
                    sb2.append("?");

                    keys[i++] = e.getKey();
                }

                sql = String.format("replace into %s(%s) values(%s)", table, sb1.toString(), sb2.toString());
            }

            Object[] res = new Object[keys.length];
            for (int i = 0; i < keys.length; i++)
                res[i] = vals.get(keys[i]);

            vv.add(res);
        }

        return db.updateAll(sql, vv);
    }

    private String strOf(Object val)
    {
        if (val == null)
            return "null";

        String str = val.toString();
        str = str.replaceAll("[\\\\]", "\\\\\\\\");
        str = str.replaceAll("[\r]", "\\\\r");
        str = str.replaceAll("[\n]", "\\\\n");
        str = str.replaceAll("[\']", "\\\\\'");
        str = str.replaceAll("[\"]", "\\\\\"");

        return "'" + str + "'";
    }

}
