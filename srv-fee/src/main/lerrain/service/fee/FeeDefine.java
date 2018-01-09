package lerrain.service.fee;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by lerrain on 2017/9/9.
 */
public class FeeDefine
{
    Double a1, a2, a3, a4;
    List b1, b2, b3, b4;
    Map c1, c2, c3, c4;

    Date begin, end;

    int freeze;
    int unit;
    int type;

    String memo;

    public FeeDefine(Date begin, Date end, int freeze, int unit, int type)
    {
        this.begin = begin;
        this.end = end;
        this.freeze = freeze;
        this.unit = unit;
        this.type = type;
    }

    public int getFreeze()
    {
        return freeze;
    }

    public void setFreeze(int freeze)
    {
        this.freeze = freeze;
    }

    public int getType()
    {
        return type;
    }

    public void setType(int type)
    {
        this.type = type;
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

    public boolean match()
    {
        Date now = new Date();

        if (begin != null && begin.after(now))
            return false;

        if (end != null && end.before(now))
            return false;

        return true;
    }
}
