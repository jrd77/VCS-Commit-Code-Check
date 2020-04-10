package com.atzuche.order.parentorder.dto;

import lombok.Data;

/**
 * 主订单相关信息
 *
 * @author pengcheng.fu
 * @date 2019/12/24 17:41
 */

@Data
public class ParentOrderDTO {

    /**
     * 主订单基本信息
     */
    private OrderDTO orderDTO;

    /**
     * 主订单状态信息
     */
    private OrderStatusDTO orderStatusDTO;

    /**
     * 主订单来源统计信息
     */
    private OrderSourceStatDTO orderSourceStatDTO;


}
