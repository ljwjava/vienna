package lerrain.service.order;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

/**
 * Created by lerrain on 2017/5/8.
 */
public class Order
{
    Long id;

    String productId;
    String productName;
    int productType; //1长期寿险 2短期健康险 3驾乘险 4自定义组合1

    Long vendorId;
    Long platformId;

    String bizNo;
    String bizMsg;

    String owner;

    BigDecimal price;

    Date createTime, modifyTime;

    Map<String, Object> factors;
    Map<String, Object> detail;
    Map<String, Object> extra;

    int type;
    int pay     = 1;    //1未付款 2已付款 3已退款 4部分退款 5支付失败 9支付异常
    int status  = 1;    //1填写中 2已提交 3成功 4失败 5终止 9异常

    public String getBizNo()
    {
        return bizNo;
    }

    public int getProductType()
    {
        return productType;
    }

    public void setProductType(int productType)
    {
        this.productType = productType;
    }

    public Long getVendorId()
    {
        return vendorId;
    }

    public void setVendorId(Long vendorId)
    {
        this.vendorId = vendorId;
    }

    public String getBizMsg()
    {
        return bizMsg;
    }

    public void setBizMsg(String bizMsg)
    {
        this.bizMsg = bizMsg;
    }

    public Long getPlatformId()
    {
        return platformId;
    }

    public void setPlatformId(Long platformId)
    {
        this.platformId = platformId;
    }

    public String getProductId()
    {
        return productId;
    }

    public void setProductId(String productId)
    {
        this.productId = productId;
    }

    public void setBizNo(String bizNo)
    {
        this.bizNo = bizNo;
    }

    public Map<String, Object> getDetail()
    {
        return detail;
    }

    public String getProductName()
    {
        return productName;
    }

    public void setProductName(String productName)
    {
        this.productName = productName;
    }

    public Date getCreateTime()
    {
        return createTime;
    }

    public void setCreateTime(Date createTime)
    {
        this.createTime = createTime;
    }

    public Date getModifyTime()
    {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime)
    {
        this.modifyTime = modifyTime;
    }

    public Map<String, Object> getFactors()
    {
        return factors;
    }

    public void setFactors(Map<String, Object> factors)
    {
        this.factors = factors;
    }

    public void setDetail(Map<String, Object> detail)
    {
        this.detail = detail;
    }

    public Map<String, Object> getExtra() {
        return extra;
    }

    public void setExtra(Map<String, Object> extra) {
        this.extra = extra;
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getOwner()
    {
        return owner;
    }

    public int getPay()
    {
        return pay;
    }

    public void setPay(int pay)
    {
        this.pay = pay;
    }

    public void setOwner(String owner)
    {
        this.owner = owner;
    }

    public int getStatus()
    {
        return status;
    }

    public void setStatus(int status)
    {
        this.status = status;
    }

    public BigDecimal getPrice()
    {
        return price;
    }

    public void setPrice(BigDecimal price)
    {
        this.price = price;
    }

    public int getType()
    {
        return type;
    }

    public void setType(int type)
    {
        this.type = type;
    }
}
