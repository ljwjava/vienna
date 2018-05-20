package lerrain.service.product.fee;

import lerrain.tool.Common;

import java.util.Date;
import java.util.Map;

/**
 * Created by lerrain on 2017/9/9.
 */
public class CustFeeDefine
{
    Long schemeId;
    Long productId;

    Map<String, Object> factors;

    Object rate;

    Date begin, end;

    int freeze;
    int unit;

    String memo;

    public Long getSchemeId()
    {
        return schemeId;
    }

    public void setSchemeId(Long schemeId)
    {
        this.schemeId = schemeId;
    }

    public Long getProductId()
    {
        return productId;
    }

    public void setProductId(Long productId)
    {
        this.productId = productId;
    }

    public Map<String, Object> getFactors()
    {
        return factors;
    }

    public void setFactors(Map<String, Object> factors)
    {
        this.factors = factors;
    }

    public Object getRate()
    {
        return rate;
    }

    public void setRate(Object rate)
    {
        this.rate = rate;
    }

    public Date getBegin()
    {
        return begin;
    }

    public void setBegin(Date begin)
    {
        this.begin = begin;
    }

    public Date getEnd()
    {
        return end;
    }

    public void setEnd(Date end)
    {
        this.end = end;
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

    public boolean match(Date now, Map<String, Object> factors)
    {
        if (begin != null && begin.after(now))
            return false;

        if (end != null && end.before(now))
            return false;

        for (Map.Entry<String, Object> e : this.factors.entrySet())
        {
            Object v1 = e.getValue();
            Object v2 = factors.get(e.getKey());

            if (v1 == null)
                continue;
            if (v2 == null)
                return false;

            if (v1 instanceof Number)
            {
                if (Common.doubleOf(v1, -1) != Common.doubleOf(v2, -2))
                    return false;
            }
            else
            {
                if (!v1.toString().equals(v2.toString()))
                    return false;
            }
        }

        return true;
    }
}
