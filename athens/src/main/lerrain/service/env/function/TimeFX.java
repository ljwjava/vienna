package lerrain.service.env.function;

import lerrain.tool.Common;
import lerrain.tool.formula.Factors;
import lerrain.tool.formula.Function;

import java.util.Date;
import java.util.HashMap;

/**
 * Created by lerrain on 2017/9/19.
 */
public class TimeFX extends HashMap<String, Object>
{
    public TimeFX()
    {
        this.put("getAge", new Function()
        {
            @Override
            public Object run(Object[] objects, Factors factors)
            {
                Date birthday = Common.dateOf(objects[0]);
                Date today = objects.length >= 2 ? Common.dateOf(objects[1]) : new Date();

                return Common.getAge(birthday, today);
            }
        });

        this.put("getAgeMonth", new Function()
        {
            @Override
            public Object run(Object[] objects, Factors factors)
            {
                Date birthday = Common.dateOf(objects[0]);
                Date today = objects.length >= 2 ? Common.dateOf(objects[1]) : new Date();

                return Common.getAgeMonth(birthday, today);
            }
        });

        this.put("getAgeDay", new Function()
        {
            @Override
            public Object run(Object[] objects, Factors factors)
            {
                Date birthday = Common.dateOf(objects[0]);
                Date today = objects.length >= 2 ? Common.dateOf(objects[1]) : new Date();

                return (today.getTime() - birthday.getTime()) / 3600000L / 24;
            }
        });
    }
}
