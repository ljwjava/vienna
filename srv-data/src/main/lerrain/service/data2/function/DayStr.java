package lerrain.service.data2.function;

import lerrain.tool.Common;
import lerrain.tool.formula.Factors;
import lerrain.tool.formula.Function;

import java.util.Date;

public class DayStr implements Function
{
    @Override
    public Object run(Object[] v, Factors p)
    {
        int windage = 0;
        if (v != null && v.length >= 1)
            windage = Common.intOf(v[0], 0);

        if (windage == 0)
            return String.format("%tF", new Date());
        else
            return String.format("%tF", new Date(new Date().getTime() + windage * 3600000L * 24));
    }
}
