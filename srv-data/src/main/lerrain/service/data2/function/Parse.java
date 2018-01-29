package lerrain.service.data2.function;

import lerrain.service.data2.DataService;
import lerrain.tool.formula.Factors;
import lerrain.tool.formula.Function;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Parse implements Function
{
    @Autowired
    DataService dataService;

    @Override
    public Object run(Object[] objects, Factors factors)
    {
        return null;//dataService.parse(objects[0].toString(), (Map)objects[1]);
    }
}
