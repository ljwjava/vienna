package lerrain.service.env.function;

import lerrain.tool.Common;
import lerrain.tool.formula.Factors;
import lerrain.tool.formula.Function;

import java.util.Calendar;
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

        this.put("long", new Function()
        {
            @Override
            public Object run(Object[] v, Factors factors)
            {
                if(v != null && v.length > 0){
                    Date t = Common.dateOf(v[0], new Date());
                    return t.getTime();
                }

                return new Date().getTime();
            }
        });

        this.put("year", new Function()
        {
            @Override
            public Object run(Object[] v, Factors factors)
            {
                Calendar cal = Calendar.getInstance();
                if (v != null && v.length > 0)
                    cal.setTime(Common.dateOf(v[0]));
                return cal.get(Calendar.YEAR);
            }
        });


        this.put("month", new Function()
        {
            @Override
            public Object run(Object[] v, Factors factors)
            {
                Calendar cal = Calendar.getInstance();
                if (v != null && v.length > 0)
                    cal.setTime(Common.dateOf(v[0]));
                return cal.get(Calendar.MONTH) + 1;
            }
        });

        this.put("day", new Function()
        {
            @Override
            public Object run(Object[] v, Factors factors)
            {
                Calendar cal = Calendar.getInstance();
                if (v != null && v.length > 0)
                    cal.setTime(Common.dateOf(v[0]));
                return cal.get(Calendar.DAY_OF_MONTH);
            }
        });

        this.put("diff", new Function()
        {
            @Override
            public Object run(Object[] v, Factors factors)
            {
                if (v != null)
                {
                    Date eTime = Common.dateOf(v[0]);	// 往后时间
                    Date sTime = Common.dateOf(v[1]);	// 往前时间
                    String t = "day";	// year,month,day,hour,min,sec

                    if(v.length == 3 && v[2] != null) {
                        t = String.valueOf(v[2]).trim();
                        if(",year,month,day,hour,min,sec,".indexOf(","+t+",") < 0){
                            return null;
                        }
                    }

                    long f = 1L;
                    if("day".equalsIgnoreCase(t)){
                        f = 3600000L * 24;
                    } else if("hour".equalsIgnoreCase(t)) {
                        f = 60 * 60 * 1000L;
                    } else if("min".equalsIgnoreCase(t)) {
                        f = 60 * 1000L;
                    } else if("sec".equalsIgnoreCase(t)) {
                        f = 1000L;
                    } else {
                        return null;
                    }

                    // TODO：此处疑似有时区问题
                    long end = (long) Math.floor(eTime.getTime() / f);
                    long start = (long) Math.floor(sTime.getTime() / f);

                    return end - start;
                }

                return null;
            }
        });
    }
}
