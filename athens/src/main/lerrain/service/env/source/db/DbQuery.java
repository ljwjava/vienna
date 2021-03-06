package lerrain.service.env.source.db;

import lerrain.tool.formula.Factors;
import lerrain.tool.formula.Function;

import java.util.Map;

public class DbQuery implements Function
{
    DataBase db;

    public DbQuery(DataBase db)
    {
        this.db = db;
    }

    @Override
    public Object run(Object[] objects, Factors factors)
    {
        return db.queryList((String)objects[0], objects.length >= 2 ? (Object[])objects[1] : null, objects.length >= 3 ? (Map<String, String>)objects[2] : null);
    }
}
