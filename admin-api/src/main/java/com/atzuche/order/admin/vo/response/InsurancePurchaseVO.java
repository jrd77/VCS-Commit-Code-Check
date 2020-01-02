package com.atzuche.order.admin.vo.response;

/**
 *
 */
public class InsurancePurchaseVO {

    private String orderNo;
    private String insuranceCompany;
    private String insuranceNo;
    private String insuranceFee;
    private String insuranceDate;
    private String insuranceStart;
    private String insuranceEnd;

    /**
     * 1 手工 2 导入 3BI
     */
    private int type;

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getInsuranceCompany() {
        return insuranceCompany;
    }

    public void setInsuranceCompany(String insuranceCompany) {
        this.insuranceCompany = insuranceCompany;
    }

    public String getInsuranceNo() {
        return insuranceNo;
    }

    public void setInsuranceNo(String insuranceNo) {
        this.insuranceNo = insuranceNo;
    }

    public String getInsuranceFee() {
        return insuranceFee;
    }

    public void setInsuranceFee(String insuranceFee) {
        this.insuranceFee = insuranceFee;
    }

    public String getInsuranceDate() {
        return insuranceDate;
    }

    public void setInsuranceDate(String insuranceDate) {
        this.insuranceDate = insuranceDate;
    }

    public String getInsuranceStart() {
        return insuranceStart;
    }

    public void setInsuranceStart(String insuranceStart) {
        this.insuranceStart = insuranceStart;
    }

    public String getInsuranceEnd() {
        return insuranceEnd;
    }

    public void setInsuranceEnd(String insuranceEnd) {
        this.insuranceEnd = insuranceEnd;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "InsurancePurchaseVO{" +
                "orderNo='" + orderNo + '\'' +
                ", insuranceCompany='" + insuranceCompany + '\'' +
                ", insuranceNo='" + insuranceNo + '\'' +
                ", insuranceFee='" + insuranceFee + '\'' +
                ", insuranceDate='" + insuranceDate + '\'' +
                ", insuranceStart='" + insuranceStart + '\'' +
                ", insuranceEnd='" + insuranceEnd + '\'' +
                ", type=" + type +
                '}';
    }
}
