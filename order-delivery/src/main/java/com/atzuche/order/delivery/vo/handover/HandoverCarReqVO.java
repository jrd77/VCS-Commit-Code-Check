package com.atzuche.order.delivery.vo.handover;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class HandoverCarReqVO {

    /**
     * 车主子订单号
     */
    private String ownerOrderNo;
    /**
     * 租客子订单号
     */
    private String renterOrderNo;
}
