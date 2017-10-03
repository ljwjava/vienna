package lerrain.service.sale;

import lerrain.tool.Common;
import lerrain.tool.formula.Formula;
import lerrain.tool.script.Stack;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class KeyValService
{
    Map map = new HashMap();

    public void setValue(String key, Object value)
    {
        map.put(key, value);
    }

    public Object getValue(String key)
    {
        return map.get(key);
    }
}
