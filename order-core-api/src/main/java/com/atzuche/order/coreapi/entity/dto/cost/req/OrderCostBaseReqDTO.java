package com.atzuche.order.coreapi.entity.dto.cost.req;

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
    /**
     * 租客会员号
     */
    private String memNo;
    /**
     * 车主会员号
     */
    private String ownerMemNo;
    /**
     * 订单取车时间(yyyy-MM-dd HH:mm:ss)
     */
    private LocalDateTime startTime;
    /**
     * 订单还车时间(yyyy-MM-dd HH:mm:ss)
     */
    private LocalDateTime endTime;
    /*
    * 订单类型
    * */
    private String orderCategory;

}
