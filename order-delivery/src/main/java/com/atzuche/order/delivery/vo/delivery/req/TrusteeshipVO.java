package com.atzuche.order.delivery.vo.delivery.req;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class TrusteeshipVO {

    @AutoDocProperty("订单号")
    private String orderNo;
    @AutoDocProperty("车牌号")
    private String carNo;

}