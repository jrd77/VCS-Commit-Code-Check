package com.atzuche.order.admin.entity;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class OrderInsuranceAdditionRequestEntity {
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

    @AutoDocProperty(value="操作人")
    private String operator;

}
