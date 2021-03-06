package lerrain.service.product.fee;

import com.alibaba.fastjson.JSON;
import lerrain.tool.Common;

import java.util.Date;
import java.util.Map;

/**
 * Created by lerrain on 2017/9/13.
 */
public class Fee
{
    Long id;
    Long platformId;
    Long vendorId;
    Long payer;
    Long drawer;

    int bizType;
    Long bizId;

    double amount;
    boolean auto; //是否自动支付

    String productId; //对应的产品id，考虑活动code设置为string
    String bizNo; //对应的业务流水号，一般是保单号
    String memo; //备注

    int type; //类型，0未知 1佣金 2关联账号奖励 3渠道费用 4奖金
    int unit; //单位：1rmb 2转发基础 3积分
    int freeze; //支付后冻结一定时间
    int status = 0; //状态：0未付 1已付 2锁定 9失败

    Date estimate; //预计支付时间
    Date payTime; //实际支付时间
    Date createTime;

    Map extra;   // 扩展信息

    public double getAmount()
    {
        return amount;
    }

    public void setAmount(double amount)
    {
        this.amount = amount;
    }

    public boolean isAuto()
    {
        return auto;
    }

    public void setAuto(boolean auto)
    {
        this.auto = auto;
    }

    public String getBizNo()
    {
        return bizNo;
    }

    public void setBizNo(String bizNo)
    {
        this.bizNo = bizNo;
    }

    public Date getCreateTime()
    {
        return createTime;
    }

    public void setCreateTime(Date createTime)
    {
        this.createTime = createTime;
    }

    public Date getEstimate()
    {
        return estimate;
    }

    public void setEstimate(Date estimate)
    {
        this.estimate = estimate;
    }

    public int getFreeze()
    {
        return freeze;
    }

    public void setFreeze(int freeze)
    {
        this.freeze = freeze;
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getMemo()
    {
        return memo;
    }

    public void setMemo(String memo)
    {
        this.memo = memo;
    }

    public Map getExtra() {
        return extra;
    }

    public void setExtra(Map extra) {
        this.extra = extra;
    }

    public int getUnit()
    {
        return unit;
    }

    public void setUnit(int unit)
    {
        this.unit = unit;
    }

    public Date getPayTime()
    {
        return payTime;
    }

    public void setPayTime(Date payTime)
    {
        this.payTime = payTime;
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

    public int getType()
    {
        return type;
    }

    public void setType(int type)
    {
        this.type = type;
    }

    public int getStatus()
    {
        return status;
    }

    public void setStatus(int status)
    {
        this.status = status;
    }

    public Long getPayer()
    {
        return payer;
    }

    public void setPayer(Long payer)
    {
        this.payer = payer;
    }

    public Long getDrawer()
    {
        return drawer;
    }

    public void setDrawer(Long drawer)
    {
        this.drawer = drawer;
    }

    public int getBizType()
    {
        return bizType;
    }

    public void setBizType(int bizType)
    {
        this.bizType = bizType;
    }

    public Long getBizId()
    {
        return bizId;
    }

    public void setBizId(Long bizId)
    {
        this.bizId = bizId;
    }

    public Long getVendorId()
    {
        return vendorId;
    }

    public void setVendorId(Long vendorId)
    {
        this.vendorId = vendorId;
    }
}
