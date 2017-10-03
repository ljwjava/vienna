package lerrain.service.sale.function;

import lerrain.service.sale.KeyValService;
import lerrain.service.sale.VendorService;
import lerrain.service.sale.WareDao;
import lerrain.service.sale.WareService;
import lerrain.tool.Common;
import lerrain.tool.formula.Factors;
import lerrain.tool.formula.Function;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

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
