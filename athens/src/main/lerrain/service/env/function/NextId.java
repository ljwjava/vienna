package lerrain.service.env.function;

import lerrain.service.common.ServiceMgr;
import lerrain.tool.formula.Factors;
import lerrain.tool.formula.Function;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class NextId implements Function
{
    @Autowired
    ServiceMgr serviceMgr;

    Map<String, long[]> map = new HashMap<>();

    @Override
    public Object run(Object[] objects, Factors factors)
    {
        String key = objects == null || objects.length == 0 ? "common" : objects[0].toString();
        return serviceMgr.nextId(key);
   }
}
