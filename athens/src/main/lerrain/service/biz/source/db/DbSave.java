package lerrain.service.biz.source.db;

import lerrain.tool.Common;
import lerrain.tool.formula.Factors;
import lerrain.tool.formula.Function;

import java.util.Date;
import java.util.Map;

public class DbSave implements Function
{
    DataBase db;

    public DbSave(DataBase db)
    {
        this.db = db;
    }

    @Override
    public Object run(Object[] objects, Factors factors)
    {
        String table = Common.trimStringOf(objects[0]);
        String idCol = Common.trimStringOf(objects[1]);
        Map<String, Object> vals = (Map)objects[2];

        if (objects.length >= 4)
        {
            Map<String, String> cols = (Map)objects[3];
            for (Map.Entry<String, String> e : cols.entrySet())
            {
                if ("datetime".equals(e.getValue().toLowerCase()))
                {
                    String key = e.getKey();
                    Long time = Common.toLong(vals.get(key));
                    vals.put(key, time == null ? null : Common.getString(new Date(time),null));
                }
            }
        }

        String res = null;

        String sql = String.format("select * from %s where %s = %s", table, idCol, vals.get(idCol));
        Map old = db.queryMap(sql, null);

        if (old != null)
        {
            Date d1 = Common.dateOf(old.get("gmt_modified"));
            Date d2 = Common.dateOf(vals.get("gmt_modified"));

            if (d1 == null || d2 == null || d2.after(d1))
            {
                StringBuffer sb = null;
                for (Map.Entry e : vals.entrySet())
                {
                    if (!idCol.equals(e.getKey()))
                    {
                        if (sb == null)
                            sb = new StringBuffer();
                        else
                            sb.append(",");

                        sb.append(e.getKey() + "=" + strOf(e.getValue()));
                    }
                }

                res = String.format("update %s set %s where %s=%s", table, sb.toString(), idCol, strOf(vals.get(idCol)));
            }
        }
        else
        {
            StringBuffer sb1 = null;
            StringBuffer sb2 = null;
            for (Map.Entry e : vals.entrySet())
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
                sb2.append(strOf(e.getValue()));
            }

            res = String.format("insert into %s(%s) values(%s)", table, sb1.toString(), sb2.toString());
        }

        if (res != null)
            db.update(res, null);

        return null;
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
