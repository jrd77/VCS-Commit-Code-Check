package com.atzuche.order.admin.dto;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.NotBlank;

@Data
@ToString
public class OrderRiskStatusRequestDTO {

    @AutoDocProperty(value = "订单号")
    @NotBlank(message = "订单号不能为空")
    private String orderNo;

    @AutoDocProperty(value = "是否有风控事故,1:有,0:无")
    @NotBlank(message = "是否有风控事故不能为空")
    private String riskAccidentStatus;

    @AutoDocProperty(value = "操作人")
    private String operator;


}
