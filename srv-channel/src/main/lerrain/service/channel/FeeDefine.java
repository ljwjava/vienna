package lerrain.service.channel;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

/**
 * Created by lerrain on 2017/9/9.
 */
public class FeeDefine
{
    Long platformId;
    Long contractId;
    Long drawer, payer;

    Long productId;
    BigDecimal[] feeRate;

    int unit;

    Date begin, end;

    public Long getPlatformId()
    {
        return platformId;
    }

    public void setPlatformId(Long platformId)
    {
        this.platformId = platformId;
    }

    public Long getContractId()
    {
        return contractId;
    }

    public void setContractId(Long contractId)
    {
        this.contractId = contractId;
    }

    public BigDecimal[] getFeeRate()
    {
        return feeRate;
    }

    public void setFeeRate(BigDecimal[] feeRate)
    {
        this.feeRate = feeRate;
    }

    public int getUnit()
    {
        return unit;
    }

    public void setUnit(int unit)
    {
        this.unit = unit;
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

    public Long getProductId()
    {
        return productId;
    }

    public void setProductId(Long productId)
    {
        this.productId = productId;
    }

    public Long getDrawer()
    {
        return drawer;
    }

    public void setDrawer(Long drawer)
    {
        this.drawer = drawer;
    }

    public Long getPayer()
    {
        return payer;
    }

    public void setPayer(Long payer)
    {
        this.payer = payer;
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
