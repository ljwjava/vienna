package lerrain.service.env.function;

import lerrain.service.biz.GatewayService;
import lerrain.service.env.ScriptErrorException;
import lerrain.tool.Common;
import lerrain.tool.formula.Factors;
import lerrain.tool.formula.Function;
import lerrain.tool.script.Stack;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class OnError implements Function
{
    @Autowired
    GatewayService gatewaySrv;

    @Override
    public Object run(Object[] objects, Factors factors)
    {
        throw new ScriptErrorException(factors, Common.trimStringOf(objects[0]), objects.length < 2 ? null : Common.trimStringOf(objects[1]));
    }
}
