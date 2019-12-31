package com.atzuche.order.delivery.vo.delivery;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author 胡春林
 * 取还车信息
 */
@Data
@ToString
public class OrderDeliveryVO implements Serializable {

    /**
     * 租客配送订单信息
     */
    private OrderDeliveryDTO orderDeliveryDTO;
    /**
     * 仁云需要的数据
     */
    private RenYunFlowOrderDTO renYunFlowOrderDTO;
    /**
     * 租客配送地址信息
     */
    private RenterDeliveryAddrDTO renterDeliveryAddrDTO;
}
