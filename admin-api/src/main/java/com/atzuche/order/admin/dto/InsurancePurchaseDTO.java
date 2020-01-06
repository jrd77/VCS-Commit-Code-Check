package com.atzuche.order.admin.dto;

import com.autoyol.doc.annotation.AutoDocProperty;

/**
 * Created by dongdong.zhao on 2019/1/29.
 */
public class InsurancePurchaseDTO {


    @AutoDocProperty(value="订单号")
    private String orderNo;
    @AutoDocProperty(value="保险公司")
    private String insuranceCompany;
    @AutoDocProperty(value="保单号")
    private String insuranceNo;
    @AutoDocProperty(value="保单费")
    private String insuranceFee;
    @AutoDocProperty(value="保单生成日期")
    private String insuranceDate;
    @AutoDocProperty(value="保单起期")
    private String insuranceStart;
    @AutoDocProperty(value="保单止期")
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
        return "InsurancePurchaseDTO{" +
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
