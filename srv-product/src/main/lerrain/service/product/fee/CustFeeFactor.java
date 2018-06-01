package lerrain.service.product.fee;

import lerrain.tool.Common;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by lerrain on 2017/9/9.
 */
public class CustFeeFactor
{
    Long wareId;
    List<Long> productIds;

    Map<String, Object> factors;

    public Long getWareId()
    {
        return wareId;
    }

    public void setWareId(Long wareId)
    {
        this.wareId = wareId;
    }

    public List<Long> getProductIds()
    {
        return productIds;
    }

    public void setProductIds(List<Long> productIds)
    {
        this.productIds = productIds;
    }

    public Map<String, Object> getFactors()
    {
        return factors;
    }

    public void setFactors(Map<String, Object> factors)
    {
        this.factors = factors;
    }
}
