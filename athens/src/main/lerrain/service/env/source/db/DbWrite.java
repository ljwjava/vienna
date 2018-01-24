package lerrain.service.env.source.db;

import lerrain.tool.formula.Factors;
import lerrain.tool.formula.Function;

public class DbWrite implements Function
{
    DataBase db;

    public DbWrite(DataBase db)
    {
        this.db = db;
    }

    @Override
    public Object run(Object[] objects, Factors factors)
    {
        return db.write(objects[0].toString(), objects.length > 1 ? (Object[])objects[1] : null);
    }
}
