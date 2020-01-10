package com.atzuche.order.commons.entity.orderDetailDto;

import lombok.Data;

@Data
public class OrderStatusRespDTO {
    /**
     * 主订单状态
     */
    private OrderStatusDTO orderStatusDTO;
    /**
     * 正常生效的租客订单状态
     */
    private RenterOrderStatusDTO renterOrderStatusDTO;
    /**
     * 正常生效的车主订单状态
     */
    private OwnerOrderStatusDTO ownerOrderStatusDTO;

    private Boolean isChange;
    /**
     * 改变中的租客订单状态
     */
    private RenterOrderStatusDTO renterOrderStatusChangeDTO;
    /**
     * 改变中的车主订单状态
     */
    private OwnerOrderStatusDTO ownerOrderStatusChangeDTO;
}
