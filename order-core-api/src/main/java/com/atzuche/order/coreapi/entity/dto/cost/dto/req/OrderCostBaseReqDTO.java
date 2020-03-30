package com.atzuche.order.coreapi.entity.dto.cost.dto.req;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 基础参数(公共参数)
 *
 * @author pengcheng.fu
 * @date 2020/3/27 16:37
 */

@Data
public class OrderCostBaseReqDTO {

    /**
     * 订单取车时间(yyyy-MM-dd HH:mm:ss)
     */
    private LocalDateTime rentTime;

    /**
     * 订单还车时间(yyyy-MM-dd HH:mm:ss)
     */
    private LocalDateTime revertTime;


}
