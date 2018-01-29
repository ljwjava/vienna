package lerrain.service.data2.function;

import lerrain.service.common.ServiceTools;
import lerrain.tool.formula.Factors;
import lerrain.tool.formula.Function;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class NextId implements Function
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
