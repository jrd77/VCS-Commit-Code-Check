package com.atzuche.order.delivery.vo.delivery;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author 胡春林
 * 取消结构体
 */
@Data
@Accessors(chain = true)
public class CancelOrderDeliveryVO {

    /**
     * 租客子订单号
     */
    private String renterOrderNo;
    private CancelFlowOrderDTO cancelFlowOrderDTO;
}
