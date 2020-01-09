package com.atzuche.order.commons.entity.orderDetailDto;

import lombok.Data;

@Data
public class OrderDetailReqDTO {
    /**
     * 主订单号
     */
    private String orderNo;
    /**
     * 租客子订单号
     */
    private String renterOrderNo;
    /**
     * 车主子订单号
     */
    private String ownerOrderNo;

}
