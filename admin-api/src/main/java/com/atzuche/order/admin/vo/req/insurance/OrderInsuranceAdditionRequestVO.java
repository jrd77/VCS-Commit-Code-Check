package com.atzuche.order.admin.vo.req.insurance;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class OrderInsuranceAdditionRequestVO {
    @AutoDocProperty(value = "订单号")
    private String orderNo;

    @AutoDocProperty(value = "保险公司类型:1:太平洋保险;2:中国人民保险")
    private String insuranceCompanyType;

    @AutoDocProperty(value = "保单号")
    private String insuranceNo;

    @AutoDocProperty(value = "保险开始时间")
    private String insuranceStart;

    @AutoDocProperty(value = "保险结束时间")
    private String insuranceEnd;

    @AutoDocProperty(value = "保单保费")
    private String insuranceFee;



}
