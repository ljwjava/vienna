package lerrain.service.product.fee.function;

import lerrain.service.product.fee.Fee;
import lerrain.service.product.fee.FeeService;
import lerrain.tool.Common;
import lerrain.tool.formula.Factors;
import lerrain.tool.formula.Function;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class Store implements Function
{
    @Autowired
    FeeService feeSrv;

    @Override
    public Object run(Object[] objects, Factors factors)
    {
        int i = 0;

        Fee r = new Fee();
        r.setPlatformId(Common.toLong(factors.get("PLATFORM_ID")));
        r.setDraweeType(Common.intOf(objects[i++], 0));
        r.setDrawee(Common.toLong(objects[i++]));
        r.setPayeeType(Common.intOf(objects[i++], 0));
        r.setPayee(Common.toLong(objects[i++]));

        r.setProductId(objects[i++].toString());
        r.setBizNo(Common.trimStringOf(objects[i++]));
        r.setAmount(Common.doubleOf(objects[i++], 0));
        r.setAuto(Common.boolOf(objects[i++], false));
        r.setEstimate(Common.dateOf(objects[i++]));
        r.setType(Common.intOf(objects[i++], 0));
        r.setUnit(Common.intOf(objects[i++], 1));
        r.setFreeze(Common.intOf(objects[i++], 1));
        r.setExtra((Map)objects[i++]);
        r.setMemo(Common.trimStringOf(objects[i++]));

        if (r.getAmount() > 0 && r.getPayee() != null)
            feeSrv.store(r);

        return null;
    }
}
