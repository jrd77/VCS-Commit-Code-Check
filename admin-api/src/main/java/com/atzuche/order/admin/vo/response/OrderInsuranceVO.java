package com.atzuche.order.admin.vo.response;

import com.autoyol.doc.annotation.AutoDocProperty;

/**
 * Created by qincai.lin on 2019/12/30.
 */
public class OrderInsuranceVO {
    @AutoDocProperty(value = "订单号")
    private String orderNo;

    @AutoDocProperty(value = "保险公司")
    private String insuranceCompany;

    @AutoDocProperty(value = "保单号")
    private String insuranceNo;

    @AutoDocProperty(value = "保险开始时间")
    private String insuranceStart;

    @AutoDocProperty(value = "保险结束时间")
    private String insuranceEnd;

    @AutoDocProperty(value = "保单保费")
    private String insuranceFee;

    @AutoDocProperty(value = "保险生成时间")
    private String insuranceDate;

    @AutoDocProperty(value = "录入类型")
    private String type;

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getInsuranceCompany() {
        return insuranceCompany;
    }

    public String getInsuranceNo() {
        return insuranceNo;
    }

    public void setInsuranceNo(String insuranceNo) {
        this.insuranceNo = insuranceNo;
    }

    public String getInsuranceStart() {
        return insuranceStart;
    }

    public void setInsuranceStart(String insuranceStart) {
        this.insuranceStart = insuranceStart;
    }

    public String getInsuranceDate() {
        return insuranceDate;
    }

    public void setInsuranceDate(String insuranceDate) {
        this.insuranceDate = insuranceDate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getInsuranceFee() {
        return insuranceFee;
    }

    public void setInsuranceFee(String insuranceFee) {
        this.insuranceFee = insuranceFee;
    }

    public void setInsuranceCompany(String insuranceCompany) {
        this.insuranceCompany = insuranceCompany;
    }

    public String getInsuranceEnd() {
        return insuranceEnd;
    }

    public void setInsuranceEnd(String insuranceEnd) {
        this.insuranceEnd = insuranceEnd;
    }


}
