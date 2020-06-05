package com.atzuche.order.commons.vo.req;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

@Data
public class ClearingRefundReqVO {
    @AutoDocProperty("订单号")
    @NotBlank(message = "订单号不能为空")
    private String orderNo;

    @AutoDocProperty("支付流水号")
    @NotBlank(message = "支付流水号不能为空")
    private String payTransNo;


    @AutoDocProperty("支付方式")
    @NotBlank(message = "操作方式不能为空")
    private String payType;

    @AutoDocProperty("支付来源")
    private String paySource;
    @AutoDocProperty("金额")
    private Integer amt;

    @AutoDocProperty("操作人")
    private String operateName;
}
