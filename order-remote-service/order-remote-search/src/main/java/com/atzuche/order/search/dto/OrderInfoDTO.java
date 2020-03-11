package com.atzuche.order.search.dto;

import lombok.Data;

/**
 *
 *
 * @author pengcheng.fu
 * @date 2020/3/6 14:48
 */

@Data
public class OrderInfoDTO {

    /**
     * 主订单号
     */
    private String orderNo;

    /**
     * 订单主状态
     */
    private Integer status;

    /**
     * 0老 1新
     */
    private Integer newOrOldOrder;

}
