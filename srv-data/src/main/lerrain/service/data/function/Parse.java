package lerrain.service.data.function;

import lerrain.service.data.DataService;
import lerrain.tool.formula.Factors;
import lerrain.tool.formula.Function;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

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
