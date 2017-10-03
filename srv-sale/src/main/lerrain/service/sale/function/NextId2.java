package lerrain.service.sale.function;

import lerrain.service.common.ServiceTools;
import lerrain.tool.Common;
import lerrain.tool.formula.Factors;
import lerrain.tool.formula.Function;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Repository
public class NextId2 implements Function
{
    @Autowired
    ServiceTools tools;

    @Override
    public Object run(Object[] objects, Factors factors)
    {
        String key = objects == null || objects.length == 0 ? "common" : objects[0].toString();
        return tools.nextId(key);
   }
}
