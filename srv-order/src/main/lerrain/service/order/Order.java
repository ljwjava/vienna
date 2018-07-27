package lerrain.service.order;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by lerrain on 2017/5/8.
 */
public class Order
{
    Long id;//主键id
    Long parentId;//父类id
    String code;//编号

    String productId;//产品id
    String productCode;//产品编号
    String productName;//产品名称

    int productType; //1长期寿险 2短期健康险 3驾乘险 4建议书订单 5体检卡 6团险

    String consumer;//

    Long vendorId;//
    Long platformId;//平台id
    Long bizId;     //本地业务表对应id，与type配合使用，保险为t_policy表

    String applyNo;//投保单号
    String bizNo;//
    String bizMsg;//

    String owner;//业务员
    String ownerCompany;//业务员所属公司

    BigDecimal price;//保费

    List<Long> children;//子单

    Date createTime, modifyTime;//更新时间

    Map<String, Object> factors;//
    Map<String, Object> detail;//投保人，被保人，投保计划等 json数据集合
    Map<String, Object> extra;//支付相关json数据集合

    int type;           //2保险 3组合 4团单
    int pay     = 1;    //1未付款 2已付款 3已退款 4部分退款 5支付失败 6部分付款 9支付异常 0无状态
    int status  = 1;    // 1填写中 2已提交 3成功 4失败退回 5终止 6失败并失效 8转至其他系统处理 9异常 10待初审 11初审不通过 12待人核 13人核通过 14人核不通过 21预约中 22已预约 23预约告知 24预约失败
    int appointmentStatus = 0;  // 0未预约 1预约中 2已预约 3预约成功 4预约失败 5预约取消
    int artifUwStatus = 0;  // 0待初审 1初审失败 2待人核 3人核通过 4人核不通过

    public String getBizNo()
    {
        return bizNo;
    }

    public String getApplyNo()
    {
        return applyNo;
    }

    public void setApplyNo(String applyNo)
    {
        this.applyNo = applyNo;
    }

    public List<Long> getChildren()
    {
        return children;
    }

    public void setChildren(List<Long> children)
    {
        this.children = children;
    }

    public Long getParentId()
    {
        return parentId;
    }

    public void setParentId(Long parentId)
    {
        this.parentId = parentId;
    }

    public String getProductCode()
    {
        return productCode;
    }

    public void setProductCode(String productCode)
    {
        this.productCode = productCode;
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

    public String getConsumer()
    {
        return consumer;
    }

    public void setConsumer(String consumer)
    {
        this.consumer = consumer;
    }

    public Long getBizId()
    {
        return bizId;
    }

    public void setBizId(Long bizId)
    {
        this.bizId = bizId;
    }

    public int getType()
    {
        return type;
    }

    public void setType(int type)
    {
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getOwnerCompany() {
        return ownerCompany;
    }

    public void setOwnerCompany(String ownerCompany) {
        this.ownerCompany = ownerCompany;
    }

    public int getAppointmentStatus() {
        return appointmentStatus;
    }

    public void setAppointmentStatus(int appointmentStatus) {
        this.appointmentStatus = appointmentStatus;
    }

    public int getArtifUwStatus() {
        return artifUwStatus;
    }

    public void setArtifUwStatus(int artifUwStatus) {
        this.artifUwStatus = artifUwStatus;
    }
}
