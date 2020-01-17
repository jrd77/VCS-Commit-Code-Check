package com.atzuche.order.commons.entity.orderDetailDto;

import lombok.Data;

@Data
public class OrderStatusRespDTO {
    /**
     * 主订单
     */
    public OrderDTO orderDTO;
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
    /**
     * 是否有待生效的订单（当isChange=true时，再取renterOrderStatusChangeDTO，ownerOrderStatusChangeDTO）
     */
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
