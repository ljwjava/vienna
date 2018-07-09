package lerrain.service.policy;

import java.util.List;
import java.util.Map;

public class NormalPolicy {
    Long                      id;

    String                    applyNo;          //投保单号
    String                    policyNo;         //保单号
    String                    productName;      //产品名称

    String                    insureTime;
    String                    effectiveTime;    //保单起期
    String                    finishTime;       //保单止期

    String                    applicantName;    //投保人姓名
    String                    applicantMobile;  //投保人邮箱
    String                    applicantEmail;   //投保人手机号
    String                    applicantCertNo;  //投保人证件号
    String                    applicantCertType; //投保人证件类型

    String                    insurantName;     //被保人姓名
    String                    insurantCertNo;   //被保人证件号
    String                    insurantCertType; //被保人证件类型

    String                    relation;         //被保人与投保人关系

    Integer                   period;           //1-短期 2-长期 3-终身 4-以月日计无续期

    Double                    premium;          //保费
    String                    payAccountNo;     //付款账号
    String                    renewalAccountNo; //续交账号

    Integer                   platformId;       //保单来源 2-vienna 3-iybweb 4-云中介 5-q云保 6-保通线下',
    String                    vendor;           //保险公司
    String                    vendorPhone;      //保险公司电话
    String                    vendorLogoUrl;    //保险公司Logo

    String                    vehicleEngineNo;
    String                    vehicleFrameNo;
    String                    vehiclePlateNo;

    Boolean                   isEffective;

    Long                      wxPolicyId;

    String                    isDeleted;

    /**
     * 被保人
     */
    List<Map<String, String>> insurants;

    /**
     * 受益人
     */
    List<Map<String, String>> beneficiaries;
    /**
     * 自定义选项，Map格式 {code:"",name:"",value:""}
     */
    List<Map<String, String>> customs;
    /**
     * 保障详情,责任，Map格式 {clauseName:"",amount:2000}
     */
    List<PolicyClause>        clauses;

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

    public Double getPremium() {
        return premium;
    }

    public void setPremium(Double premium) {
        this.premium = premium;
    }

    public String getInsureTime() {
        return insureTime;
    }

    public void setInsureTime(String insureTime) {
        this.insureTime = insureTime;
    }

    public String getEffectiveTime() {
        return effectiveTime;
    }

    public void setEffectiveTime(String effectiveTime) {
        this.effectiveTime = effectiveTime;
    }

    public String getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(String finishTime) {
        this.finishTime = finishTime;
    }

    public List<PolicyClause> getClauses() {
        return clauses;
    }

    public void setClauses(List<PolicyClause> clauses) {
        this.clauses = clauses;
    }

    public List<Map<String, String>> getCustoms() {
        return customs;
    }

    public void setCustoms(List<Map<String, String>> customs) {
        this.customs = customs;
    }

    public String getVendorLogoUrl() {
        return vendorLogoUrl;
    }

    public void setVendorLogoUrl(String vendorLogoUrl) {
        this.vendorLogoUrl = vendorLogoUrl;
    }

    public List<Map<String, String>> getInsurants() {
        return insurants;
    }

    public void setInsurants(List<Map<String, String>> insurants) {
        this.insurants = insurants;
    }

    public List<Map<String, String>> getBeneficiaries() {
        return beneficiaries;
    }

    public void setBeneficiaries(List<Map<String, String>> beneficiaries) {
        this.beneficiaries = beneficiaries;
    }

    public Integer getPeriod() {
        return period;
    }

    public void setPeriod(Integer period) {
        this.period = period;
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

    public String getPayAccountNo() {
        return payAccountNo;
    }

    public void setPayAccountNo(String payAccountNo) {
        this.payAccountNo = payAccountNo;
    }

    public String getRenewalAccountNo() {
        return renewalAccountNo;
    }

    public void setRenewalAccountNo(String renewalAccountNo) {
        this.renewalAccountNo = renewalAccountNo;
    }

    public Integer getPlatformId() {
        return platformId;
    }

    public void setPlatformId(Integer platformId) {
        this.platformId = platformId;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getVendorPhone() {
        return vendorPhone;
    }

    public void setVendorPhone(String vendorPhone) {
        this.vendorPhone = vendorPhone;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getIsEffective() {
        return isEffective;
    }

    public void setIsEffective(Boolean isEffective) {
        this.isEffective = isEffective;
    }

    public Long getWxPolicyId() {
        return wxPolicyId;
    }

    public void setWxPolicyId(Long wxPolicyId) {
        this.wxPolicyId = wxPolicyId;
    }

    public String getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(String isDeleted) {
        this.isDeleted = isDeleted;
    }

}
