package lerrain.service.env.source.db;

import lerrain.tool.formula.Factors;
import lerrain.tool.formula.Function;

import java.util.Map;

public class DbFind implements Function
{
    DataBase db;

    public DbFind(DataBase db)
    {
        this.db = db;
    }

    @Override
    public Object run(Object[] objects, Factors factors)
    {
        return db.queryMap((String)objects[0], objects.length >= 2 ? (Object[])objects[1] : null, objects.length >= 3 ? (Map<String, String>)objects[2] : null);
    }
}
