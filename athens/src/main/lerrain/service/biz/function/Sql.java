package lerrain.service.biz.function;

import lerrain.service.common.ServiceMgr;
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
import java.util.Map;

@Component
public class Sql implements Factors
{
    @Autowired
    JdbcTemplate jdbc;

    Map<String, Object> map = new HashMap<>();

    public Sql()
    {
        map.put("query", new Function() {
            @Override
            public Object run(final Object[] objects, Factors factors)
            {
                final String sql = objects[0].toString();

                return jdbc.query(sql, (Object[])objects[1], new RowMapper<Map>()
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
