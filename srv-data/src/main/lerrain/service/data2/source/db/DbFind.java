package lerrain.service.data2.source.db;

import lerrain.tool.formula.Factors;
import lerrain.tool.formula.Function;

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
        return db.queryMap((String)objects[0], objects.length >= 2 ? (Object[])objects[1] : null);
    }
}
