package lerrain.service.policy;

import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSONObject;

public class Policy {
    Long               id;
    Long               orderId;
    Long               platformId;

    String             applyNo;
    String             policyNo;

    String             endorseNo;
    Date               endorseTime;

    String             productName;

    Integer            type;
    Integer            status;
    Integer            period;

    JSONObject         target;
    JSONObject         detail;
    JSONObject         fee;
    JSONObject         extra;

    Double             premium;

    Date               insureTime;
    Date               effectiveTime;
    Date               finishTime;

    Long               vendorId;
    Long               agencyId;

    Long               owner;
    Long               ownerOrg;
    Long               ownerCompany;

    List<PolicyClause> clauses;

    //自动替代

    String             applicantName;
    String             applicantMobile;
    String             applicantEmail;
    String             applicantCertNo;
    String             applicantCertType;

    String             insurantName;
    String             insurantCertNo;
    String             insurantCertType;

    String             vehicleEngineNo;
    String             vehicleFrameNo;
    String             vehiclePlateNo;

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getApplicantName() {
        return applicantName;
    }

    public void setApplicantName(String applicantName) {
        this.applicantName = applicantName;
    }

    public String getApplicantMobile() {
        return applicantMobile;
    }

    public void setApplicantMobile(String applicantMobile) {
        this.applicantMobile = applicantMobile;
    }

    public List<PolicyClause> getClauses() {
        return clauses;
    }

    public void setClauses(List<PolicyClause> clauses) {
        this.clauses = clauses;
    }

    public String getApplicantEmail() {
        return applicantEmail;
    }

    public void setApplicantEmail(String applicantEmail) {
        this.applicantEmail = applicantEmail;
    }

    public String getApplicantCertNo() {
        return applicantCertNo;
    }

    public void setApplicantCertNo(String applicantCertNo) {
        this.applicantCertNo = applicantCertNo;
    }

    public String getApplicantCertType() {
        return applicantCertType;
    }

    public void setApplicantCertType(String applicantCertType) {
        this.applicantCertType = applicantCertType;
    }

    public String getEndorseNo() {
        return endorseNo;
    }

    public void setEndorseNo(String endorseNo) {
        this.endorseNo = endorseNo;
    }

    public Date getEndorseTime() {
        return endorseTime;
    }

    public void setEndorseTime(Date endorseTime) {
        this.endorseTime = endorseTime;
    }

    public Integer getPeriod() {
        return period;
    }

    public void setPeriod(Integer period) {
        this.period = period;
    }

    public String getInsurantName() {
        return insurantName;
    }

    public void setInsurantName(String insurantName) {
        this.insurantName = insurantName;
    }

    public String getInsurantCertNo() {
        return insurantCertNo;
    }

    public void setInsurantCertNo(String insurantCertNo) {
        this.insurantCertNo = insurantCertNo;
    }

    public String getInsurantCertType() {
        return insurantCertType;
    }

    public void setInsurantCertType(String insurantCertType) {
        this.insurantCertType = insurantCertType;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getVehicleFrameNo() {
        return vehicleFrameNo;
    }

    public void setVehicleFrameNo(String vehicleFrameNo) {
        this.vehicleFrameNo = vehicleFrameNo;
    }

    public String getVehiclePlateNo() {
        return vehiclePlateNo;
    }

    public void setVehiclePlateNo(String vehiclePlateNo) {
        this.vehiclePlateNo = vehiclePlateNo;
    }

    public String getVehicleEngineNo() {
        return vehicleEngineNo;
    }

    public void setVehicleEngineNo(String vehicleEngineNo) {
        this.vehicleEngineNo = vehicleEngineNo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getPlatformId() {
        return platformId;
    }

    public void setPlatformId(Long platformId) {
        this.platformId = platformId;
    }

    public String getApplyNo() {
        return applyNo;
    }

    public void setApplyNo(String applyNo) {
        this.applyNo = applyNo;
    }

    public String getPolicyNo() {
        return policyNo;
    }

    public void setPolicyNo(String policyNo) {
        this.policyNo = policyNo;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public JSONObject getTarget() {
        return target;
    }

    public void setTarget(JSONObject target) {
        this.target = target;
    }

    public JSONObject getDetail() {
        return detail;
    }

    public void setDetail(JSONObject detail) {
        this.detail = detail;
    }

    public JSONObject getFee() {
        return fee;
    }

    public void setFee(JSONObject fee) {
        this.fee = fee;
    }

    public JSONObject getExtra() {
        return extra;
    }

    public void setExtra(JSONObject extra) {
        this.extra = extra;
    }

    public Double getPremium() {
        return premium;
    }

    public void setPremium(Double premium) {
        this.premium = premium;
    }

    public Date getInsureTime() {
        return insureTime;
    }

    public void setInsureTime(Date insureTime) {
        this.insureTime = insureTime;
    }

    public Date getEffectiveTime() {
        return effectiveTime;
    }

    public void setEffectiveTime(Date effectiveTime) {
        this.effectiveTime = effectiveTime;
    }

    public Date getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(Date finishTime) {
        this.finishTime = finishTime;
    }

    public Long getVendorId() {
        return vendorId;
    }

    public void setVendorId(Long vendorId) {
        this.vendorId = vendorId;
    }

    public Long getAgencyId() {
        return agencyId;
    }

    public void setAgencyId(Long agencyId) {
        this.agencyId = agencyId;
    }

    public Long getOwner() {
        return owner;
    }

    public void setOwner(Long owner) {
        this.owner = owner;
    }

    public Long getOwnerOrg() {
        return ownerOrg;
    }

    public void setOwnerOrg(Long ownerOrg) {
        this.ownerOrg = ownerOrg;
    }

    public Long getOwnerCompany() {
        return ownerCompany;
    }

    public void setOwnerCompany(Long ownerCompany) {
        this.ownerCompany = ownerCompany;
    }
}
