package lerrain.service.env.function;

import lerrain.service.env.util.MapUtils;
import lerrain.tool.Common;
import lerrain.tool.formula.Factors;
import lerrain.tool.formula.Function;

import java.util.HashMap;

/**
 * Created by lerrain on 2017/9/19.
 */
public class MapFX extends HashMap<String, Object>
{
    public MapFX()
    {
        this.put("getDistance", new Function()
        {
            @Override
            public Object run(Object[] p, Factors factors)
            {
                if(p == null && p.length < 4){
                    System.out.println("getDistance>>>缺少参数");
                    return null;
                }
                if(Common.isEmpty(p[0]) || Common.isEmpty(p[1]) || Common.isEmpty(p[2]) || Common.isEmpty(p[3])) {
                    System.out.println("getDistance>>>参数不能为空");
                    return null;
                }

                return MapUtils.GetDistance(Common.doubleOf(p[0],0), Common.doubleOf(p[1], 0), Common.doubleOf(p[2],0), Common.doubleOf(p[3],0));
            }
        });
    }
}
