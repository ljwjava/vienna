package lerrain.service.biz.function;

import lerrain.service.common.Log;
import lerrain.service.common.ServiceMgr;
import lerrain.tool.Common;
import lerrain.tool.formula.Factors;
import lerrain.tool.formula.Function;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class Sql implements Factors
{
    @Autowired
    JdbcTemplate jdbc;

    Map<String, Object> map = new HashMap<>();

    public Sql()
    {
        map.put("query", new Function()
        {
            @Override
            public Object run(final Object[] objects, Factors factors)
            {
                String sql = objects[0].toString();
                Object[] vals = objects.length >= 2 ? (Object[])objects[1] : null;

                return jdbc.query(sql, vals, new RowMapper<Map>()
                {
                    @Override
                    public Map mapRow(ResultSet rs, int j) throws SQLException
                    {
                        Map r = new HashMap<>();
                        ResultSetMetaData rsmd = rs.getMetaData();

                        int num = rsmd.getColumnCount();
                        for (int i=1;i<=num;i++)
                        {
                            String key = rsmd.getColumnLabel(i);
                            Object val = rs.getObject(i);

                            r.put(key, val);
                        }

                        return r;
                    }
                });
            }
        });

        map.put("select", new Function()
        {
            @Override
            public Object run(final Object[] objects, Factors factors)
            {
                StringBuffer sb = new StringBuffer("select * from ");
                sb.append(objects[0].toString());
                sb.append(" where valid is null");

                Map<String, Object> param = objects.length >= 2 ? (Map<String, Object>)objects[1] : null;
                if (param != null) for (Map.Entry<String, Object> p : param.entrySet())
                {
                    Object val = p.getValue();
                    if (val != null && !"".equals(val))
                    {
                        if (val instanceof String && ((String)val).startsWith("%"))
                            sb.append(" and " + p.getKey() + " like '" + val + "'");
                        else
                            sb.append(" and " + p.getKey() + " = '" + val + "'");
                    }
                }

                sb.append(" order by");
                List<String> orderBy = objects.length >= 3 ? (List<String>)objects[2] : null;
                if (orderBy != null && !orderBy.isEmpty())
                    for (String ob : orderBy)
                        sb.append(" " + ob + ",");
                sb.append(" create_time desc");

                int from = objects.length >= 4 ? Common.intOf(objects[3], 0) : 0;
                int num = objects.length >= 5 ? Common.intOf(objects[4], 0) : 20;
                sb.append(" limit " + from + "," + num);

                Log.info(sb.toString());

                return jdbc.query(sb.toString(), new RowMapper<Map>()
                {
                    @Override
                    public Map mapRow(ResultSet rs, int j) throws SQLException
                    {
                        Map r = new HashMap<>();
                        ResultSetMetaData rsmd = rs.getMetaData();

                        int num = rsmd.getColumnCount();
                        for (int i=1;i<=num;i++)
                        {
                            String key = rsmd.getColumnLabel(i);
                            Object val = rs.getObject(i);

                            r.put(key, val);
                        }

                        return r;
                    }
                });
            }
        });
    }

    @Override
    public Object get(String s)
    {
        return map.get(s);
    }
}
