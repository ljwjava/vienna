package lerrain.service.sale.function;

import lerrain.service.sale.CmsService;
import lerrain.service.sale.WareCms;
import lerrain.tool.Common;
import lerrain.tool.formula.Factors;
import lerrain.tool.formula.Function;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class Commission implements Function
{
    @Autowired
    CmsService cmsSrv;

    @Override
    public Object run(Object[] objects, Factors factors)
    {
        List<Map> r = new ArrayList<>();

        List<WareCms> list = cmsSrv.getCommissionRate(Common.toLong(factors.get("platformId")), (String)objects[0], Common.toLong(objects[1]), (String)objects[2], (String)objects[3]);
        if (list != null)
        {
            for (WareCms c : list)
            {
                Map m = new HashMap();
                m.put("self", c.getSelfRate());
                m.put("parent", c.getParentRate());
                m.put("unit", c.getUnit());
                m.put("freeze", c.getFreeze());
                m.put("memo", c.getMemo());

                r.add(m);
            }
        }

        return r;
    }
}
