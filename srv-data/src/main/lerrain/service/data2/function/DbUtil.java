package lerrain.service.data2.function;

import com.alibaba.fastjson.JSONObject;
import lerrain.tool.formula.Factors;
import lerrain.tool.formula.Function;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lerrain on 2017/9/19.
 */
public class DbUtil extends HashMap<String, Object>
{
    @Autowired
    JdbcTemplate jdbc;

    public DbUtil()
    {
        this.put("query", new Function()
        {
            @Override
            public Object run(Object[] v, Factors p)
            {
                String sql = (String)v[0];
                Object[] val = v.length > 1 ? (Object[])v[1] : null;

                return jdbc.query(sql, val, new RowMapper<JSONObject>()
                {
                    @Override
                    public JSONObject mapRow(ResultSet rs, int arg1) throws SQLException
                    {
                        JSONObject v = new JSONObject();
                        ResultSetMetaData rsmd = rs.getMetaData();

                        int num = rsmd.getColumnCount();
                        for (int i=1;i<=num;i++)
                        {
                            String key = rsmd.getColumnLabel(i);
                            Object val = rs.getObject(i);

                            v.put(key, val);
                        }

                        return v;
                    }
                });
            }
        });

        this.put("save", new Function()
        {
            @Override
            public Object run(Object[] v, Factors p)
            {
                String table = (String)v[0];
                Map<String, Object> map = (Map)v[1];

                String s1 = null, s2 = null;
                for (Map.Entry<String, Object> item : map.entrySet())
                {
                    s1 = (s1 == null ? "" : ",") + item.getKey();
                    s2 = (s2 == null ? "" : ",") + "?";
                }

                String sql = String.format("insert into %s(%s) values(%s)", table, s1, s2);
                return jdbc.update(sql, map.values().toArray());
            }
        });
    }
}
