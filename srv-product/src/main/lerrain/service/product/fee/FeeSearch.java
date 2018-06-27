package lerrain.service.product.fee;

import com.alibaba.fastjson.JSONObject;
import lerrain.tool.Common;

import java.util.Date;

public class FeeSearch
{
    Long id;
    Long platformId;
    Long vendorId;  // 产品供应商
    Long payer;     // 付款方（机构）
    Long drawer;    // 收款方（个人）

    Integer bizType;    // 0-未知 2-保单
    Long bizId;     // bizType=2时为policyId

    Boolean auto; //是否自动支付

    String productId; //对应的产品id，考虑活动code设置为string
    String bizNo; //对应的业务流水号，一般是保单号

    Integer type; //类型，0未知 1佣金 2关联账号奖励 3渠道费用 4奖金
    Integer unit; //单位：1rmb 2转发基础 3积分
    Integer freezeMin; //支付后冻结一定时间
    Integer freezeMax; //支付后冻结一定时间
    Integer status; //状态：0未付 1已付 2锁定 3线下结算 8撤销 9失败

    Date estimateB; //预计支付时间-开始
    Date estimateE; //预计支付时间-结束
    Date payTimeB; //实际支付时间-开始
    Date payTimeE; //实际支付时间-结束
    Date createTimeB;
    Date createTimeE;

    Long pageNo;    // 第几页，从1开始
    Long pageSize;  // 每页记录数

    public void fsOf(JSONObject c){
        this.id = c.getLong("id");
        this.platformId = c.getLong("platformId");
        this.vendorId = c.getLong("vendorId");
        this.payer = c.getLong("payer");
        this.drawer = c.getLong("drawer");
        this.bizType = c.getInteger("bizType");
        this.bizId = c.getLong("bizId");
        this.auto = c.getBoolean("auto");
        this.productId = c.getString("productId");
        this.bizNo = c.getString("bizNo");
        this.type = c.getInteger("type");
        this.unit = c.getInteger("unit");
        this.freezeMin = c.getInteger("freezeMin");
        this.freezeMax = c.getInteger("freezeMax");
        this.status = c.getInteger("status");
        this.payTimeB = Common.dateOf(c.getString("payTimeB"), null);
        this.payTimeE = Common.dateOf(c.getString("payTimeE"), null);
        this.createTimeB = Common.dateOf(c.getString("createTimeB"), null);
        this.createTimeE = Common.dateOf(c.getString("createTimeE"), null);
        this.pageNo = c.getLong("pageNo");
        this.pageSize = c.getLong("pageSize");
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPlatformId() {
        return platformId;
    }

    public void setPlatformId(Long platformId) {
        this.platformId = platformId;
    }

    public Long getVendorId() {
        return vendorId;
    }

    public void setVendorId(Long vendorId) {
        this.vendorId = vendorId;
    }

    public Long getPayer() {
        return payer;
    }

    public void setPayer(Long payer) {
        this.payer = payer;
    }

    public Long getDrawer() {
        return drawer;
    }

    public void setDrawer(Long drawer) {
        this.drawer = drawer;
    }

    public Integer getBizType() {
        return bizType;
    }

    public void setBizType(Integer bizType) {
        this.bizType = bizType;
    }

    public Long getBizId() {
        return bizId;
    }

    public void setBizId(Long bizId) {
        this.bizId = bizId;
    }

    public Boolean getAuto() {
        return auto;
    }

    public void setAuto(Boolean auto) {
        this.auto = auto;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getBizNo() {
        return bizNo;
    }

    public void setBizNo(String bizNo) {
        this.bizNo = bizNo;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getUnit() {
        return unit;
    }

    public void setUnit(Integer unit) {
        this.unit = unit;
    }

    public Integer getFreezeMin() {
        return freezeMin;
    }

    public void setFreezeMin(Integer freezeMin) {
        this.freezeMin = freezeMin;
    }

    public Integer getFreezeMax() {
        return freezeMax;
    }

    public void setFreezeMax(Integer freezeMax) {
        this.freezeMax = freezeMax;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getEstimateB() {
        return estimateB;
    }

    public void setEstimateB(Date estimateB) {
        this.estimateB = estimateB;
    }

    public Date getEstimateE() {
        return estimateE;
    }

    public void setEstimateE(Date estimateE) {
        this.estimateE = estimateE;
    }

    public Date getPayTimeB() {
        return payTimeB;
    }

    public void setPayTimeB(Date payTimeB) {
        this.payTimeB = payTimeB;
    }

    public Date getPayTimeE() {
        return payTimeE;
    }

    public void setPayTimeE(Date payTimeE) {
        this.payTimeE = payTimeE;
    }

    public Date getCreateTimeB() {
        return createTimeB;
    }

    public void setCreateTimeB(Date createTimeB) {
        this.createTimeB = createTimeB;
    }

    public Date getCreateTimeE() {
        return createTimeE;
    }

    public void setCreateTimeE(Date createTimeE) {
        this.createTimeE = createTimeE;
    }

    public Long getPageNo() {
        return pageNo;
    }

    public void setPageNo(Long pageNo) {
        this.pageNo = pageNo;
    }

    public Long getPageSize() {
        return pageSize;
    }

    public void setPageSize(Long pageSize) {
        this.pageSize = pageSize;
    }

}
