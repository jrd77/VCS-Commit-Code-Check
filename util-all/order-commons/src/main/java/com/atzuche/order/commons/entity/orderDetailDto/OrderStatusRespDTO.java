package com.atzuche.order.commons.entity.orderDetailDto;

import lombok.Data;

@Data
public class OrderStatusRespDTO {
    /**
     * 主订单状态
     */
    public OrderStatusDTO orderStatusDTO;
    /**
     * 正常生效的租客订单状态
     */
    public RenterOrderStatusDTO renterOrderStatusDTO;
    /**
     * 正常生效的车主订单状态
     */
    public OwnerOrderStatusDTO ownerOrderStatusDTO;

    public Boolean isChange;
    /**
     * 改变中的租客订单状态
     */
    public RenterOrderStatusDTO renterOrderStatusChangeDTO;
    /**
     * 改变中的车主订单状态
     */
    public OwnerOrderStatusDTO ownerOrderStatusChangeDTO;
}
