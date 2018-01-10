package lerrain.service.fee.function;

import com.alibaba.fastjson.JSON;
import lerrain.service.fee.FeeService;
import lerrain.tool.Common;
import lerrain.tool.formula.Factors;
import lerrain.tool.formula.Function;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FindFee implements Function
{
    @Autowired
    FeeService feeSrv;

    @Override
    public Object run(Object[] objects, Factors factors)
    {
        return feeSrv.getFeeRate(Common.toLong(objects[0]), Common.toLong(objects[1]), objects[2].toString(), objects[3].toString(), (Object[])objects[4]);
    }
}
