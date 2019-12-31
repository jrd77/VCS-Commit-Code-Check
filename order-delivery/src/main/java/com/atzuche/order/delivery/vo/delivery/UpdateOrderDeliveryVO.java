package com.atzuche.order.delivery.vo.delivery;

import lombok.Data;

/**
 * @author 胡春林
 * 更新信息
 */
@Data
public class UpdateOrderDeliveryVO {

    /**
     * 租客配送订单信息
     */
    private OrderDeliveryDTO orderDeliveryDTO;
    /**
     * 更新仁云数据
     */
    private UpdateFlowOrderDTO updateFlowOrderDTO;
    /**
     * 租客配送地址信息
     */
    private RenterDeliveryAddrDTO renterDeliveryAddrDTO;
}
