package lerrain.service.commission;

import java.util.Date;

/**
 * Created by lerrain on 2017/9/9.
 */
public class CmsDefine
{
    double[] self, parent;

    Date begin, end;

    int freeze;
    int unit;
    int type;

    String memo;

    public CmsDefine(Date begin, Date end, double[] self, double[] parent, int freeze, int unit, int type)
    {
        this.begin = begin;
        this.end = end;
        this.freeze = freeze;
        this.unit = unit;
        this.type = type;

        this.self = self;
        this.parent = parent;
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

    public double[] getSelfRate()
    {
        return self;
    }

    public double[] getSelfRate(double[] t)
    {
        return mix(self, t);
    }

    public double[] getParentRate()
    {
        return parent;
    }

    public double[] getParentRate(double[] t)
    {
        return mix(parent, t);
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

    private double[] mix(double[] r, double[] t)
    {
        if (t == null)
            return r;

        double[] x = new double[Math.max(r.length, t.length)];

        for (int i=0;i<x.length;i++)
            x[i] = (r.length > i ? r[i] : 0) + (t.length > i ? t[i] : 0);

        return x;
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
