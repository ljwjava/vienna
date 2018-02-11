package lerrain.service.fee;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by lerrain on 2017/9/9.
 */
public class FeeDefine
{
    Object f1, f2, f3, f4, f5;

    Date begin, end;

    int freeze;
    int unit;

    String memo;

    public FeeDefine(Date begin, Date end, int freeze, int unit)
    {
        this.begin = begin;
        this.end = end;
        this.freeze = freeze;
        this.unit = unit;
    }

    public Object getF1()
    {
        return f1;
    }

    public void setF1(Object f1)
    {
        this.f1 = f1;
    }

    public Object getF2()
    {
        return f2;
    }

    public void setF2(Object f2)
    {
        this.f2 = f2;
    }

    public Object getF3()
    {
        return f3;
    }

    public void setF3(Object f3)
    {
        this.f3 = f3;
    }

    public Object getF4()
    {
        return f4;
    }

    public void setF4(Object f4)
    {
        this.f4 = f4;
    }

    public Object getF5()
    {
        return f5;
    }

    public void setF5(Object f5)
    {
        this.f5 = f5;
    }

    public int getFreeze()
    {
        return freeze;
    }

    public void setFreeze(int freeze)
    {
        this.freeze = freeze;
    }

    public int getUnit()
    {
        return unit;
    }

    public void setUnit(int unit)
    {
        this.unit = unit;
    }

    public String getMemo()
    {
        return memo;
    }

    public void setMemo(String memo)
    {
        this.memo = memo;
    }

    public boolean match(Date now)
    {
        if (begin != null && begin.after(now))
            return false;

        if (end != null && end.before(now))
            return false;

        return true;
    }
}
