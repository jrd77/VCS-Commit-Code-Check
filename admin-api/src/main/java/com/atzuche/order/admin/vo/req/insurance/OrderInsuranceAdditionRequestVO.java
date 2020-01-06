package com.atzuche.order.admin.vo.req.insurance;

import com.autoyol.doc.annotation.AutoDocProperty;

/**
 * Created by qincai.lin on 2019/12/30.
 */
public class OrderInsuranceAdditionRequestVO {
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

    public String getInsuranceFee() {
        return insuranceFee;
    }

    public void setInsuranceFee(String insuranceFee) {
        this.insuranceFee = insuranceFee;
    }


}
