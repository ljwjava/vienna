package lerrain.service.biz.function;

import lerrain.tool.formula.Factors;
import lerrain.tool.formula.Function;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Repository
public class NextId implements Function
{
    @Autowired
    JdbcTemplate   jdbc;

    Map<Object, Long> current = new HashMap<>();

//    @PostConstruct
    private void loadAll()
    {
        current.clear();

        jdbc.query("select * from t_sequence", new RowMapper<Object>()
        {
            @Override
            public Object mapRow(ResultSet rs, int rowNum) throws SQLException
            {
                current.put(rs.getString("code"), rs.getLong("value"));

                return null;
            }
        });
    }

    @Override
    public Object run(Object[] objects, Factors factors)
    {
        final Object key = objects == null || objects.length == 0 ? "common" : objects[0];
        final Long cc;

        synchronized (current)
        {
            Long c = current.get(key);
            if (c == null)
                throw new RuntimeException("sequence " + key + " 不存在");

            cc = c + 1;
            current.put(key, cc);
        }

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                refresh(key, cc);
            }
        }).start();

        return cc;
    }

    private synchronized void refresh(Object key, Object v)
    {
        jdbc.update("update t_sequence set value = ? where name = ? and value < ?", v, key, v);
    }
}
