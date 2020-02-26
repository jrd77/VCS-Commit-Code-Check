package com.atzuche.order.admin.vo.req.insurance;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.NotBlank;

@Data
@ToString
public class OrderInsuranceAdditionRequestVO {
    @AutoDocProperty(value = "订单号")
    @NotBlank(message = "订单号不能为空")
    private String orderNo;

    @AutoDocProperty(value = "保险公司类型:1:太平洋保险;2:中国人民保险")
    @NotBlank(message = "保险公司类型不能为空")
    private String insuranceCompanyType;

    @AutoDocProperty(value = "保单号")
    @NotBlank(message = "保单号不能为空")
    private String insuranceNo;

    @AutoDocProperty(value = "保险开始时间")
    @NotBlank(message = "保险开始时间不能为空")
    private String insuranceStart;

    @AutoDocProperty(value = "保险结束时间")
    @NotBlank(message = "保险结束时间不能为空")
    private String insuranceEnd;

    @AutoDocProperty(value = "保单保费")
    @NotBlank(message = "保单保费不能为空")
    private String insuranceFee;



}
