package com.atzuche.order.admin.vo.req.insurance;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.NotBlank;

@Data
@ToString
public class OrderInsuranceImportRequestVO {

    @AutoDocProperty(value = "保险公司")
    @NotBlank(message = "保险公司不能为空")
    private String insuranceCompany;

    @AutoDocProperty(value = "保单号")
    @NotBlank(message = "保单号不能为空")
    private String insuranceNo;



}
