package lerrain.service.biz.function;

import lerrain.service.biz.VendorService;
import lerrain.tool.Common;
import lerrain.tool.formula.Factors;
import lerrain.tool.formula.Function;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GetVendor implements Function
{
    @Autowired
    VendorService srv;

    @Override
    public Object run(Object[] objects, Factors factors)
    {
        return srv.getVendor(Common.toLong(objects[0]));
    }
}
