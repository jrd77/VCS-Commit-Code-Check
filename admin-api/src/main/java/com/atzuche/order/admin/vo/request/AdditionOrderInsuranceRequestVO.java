package com.atzuche.order.admin.vo.request;

import com.autoyol.doc.annotation.AutoDocProperty;

/**
 * Created by qincai.lin on 2019/12/30.
 */
public class AdditionOrderInsuranceRequestVO {
    @AutoDocProperty(value = "订单号")
    private String orderNo;

    @AutoDocProperty(value = "保险公司")
    private String insuranceCompany;

    @AutoDocProperty(value = "保单号")
    private String insuranceNumber;

    @AutoDocProperty(value = "保险开始时间")
    private String startTime;

    @AutoDocProperty(value = "保险结束时间")
    private String endTime;

    @AutoDocProperty(value = "保单保费")
    private String insuranceCost;

    @AutoDocProperty(value = "保险生成时间")
    private String createTime;

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

    public String getInsuranceNumber() {
        return insuranceNumber;
    }

    public void setInsuranceNumber(String insuranceNumber) {
        this.insuranceNumber = insuranceNumber;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getInsuranceCost() {
        return insuranceCost;
    }

    public void setInsuranceCost(String insuranceCost) {
        this.insuranceCost = insuranceCost;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }



}
