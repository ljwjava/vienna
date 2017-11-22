package lerrain.service.biz.source.db;

import lerrain.tool.formula.Factors;
import lerrain.tool.formula.Function;

public class DbColumn implements Function
{
    DataBase db;

    public DbColumn(DataBase db)
    {
        this.db = db;
    }

    @Override
    public Object run(Object[] objects, Factors factors)
    {
        return db.getColumnType((String)objects[0], objects.length >= 2 ? (Object[])objects[1] : null);
    }
}
