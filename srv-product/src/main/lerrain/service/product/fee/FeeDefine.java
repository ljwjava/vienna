package lerrain.service.product.fee;

import java.util.Date;
import java.util.Map;

/**
 * Created by lerrain on 2017/9/9.
 */
public class FeeDefine
{
    Long id;
    Long schemeId;
    Long productId;

    Map factors;

    Object saleFee;
    Object saleBonus;
    Object upperBonus;

    Date begin, end;

    int freeze;
    int unit;

    String memo;

    public Long getProductId()
    {
        return productId;
    }

    public void setProductId(Long productId)
    {
        this.productId = productId;
    }

    public Long getSchemeId()
    {
        return schemeId;
    }

    public void setSchemeId(Long schemeId)
    {
        this.schemeId = schemeId;
    }

    public Map getFactors()
    {
        return factors;
    }

    public void setFactors(Map factors)
    {
        this.factors = factors;
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

    public Object getSaleFee()
    {
        return saleFee;
    }

    public void setSaleFee(Object saleFee)
    {
        this.saleFee = saleFee;
    }

    public Object getSaleBonus()
    {
        return saleBonus;
    }

    public void setSaleBonus(Object saleBonus)
    {
        this.saleBonus = saleBonus;
    }

    public Object getUpperBonus()
    {
        return upperBonus;
    }

    public void setUpperBonus(Object upperBonus)
    {
        this.upperBonus = upperBonus;
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
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
