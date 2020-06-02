package com.atzuche.order.coreapi.entity.request;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;

@Data
public class ClearingRefundReqVO {
    @AutoDocProperty("订单号")
    private String orderNo;
    @AutoDocProperty("支付流水号")
    private String payTransNo;
    @AutoDocProperty("支付来源")
    private String paySource;
    @AutoDocProperty("支付方式")
    private String payType;
    @AutoDocProperty("金额")
    private Integer amt;
}
