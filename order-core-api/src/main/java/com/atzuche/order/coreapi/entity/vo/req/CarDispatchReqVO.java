package com.atzuche.order.coreapi.entity.vo.req;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 判断订单是否可以进调度
 *
 * @author pengcheng.fu
 * @date 2020/1/8 11:31
 */

@Data
public class CarDispatchReqVO {

    /**
     * 1.车主拒单 2.车主取消订单
     */
    private Integer type;
    /**
     * 城市编号
     */
    private Integer cityCode;

    /**
     * 下单时间
     */
    private LocalDateTime reqTime;
    /**
     * 开始租期
     */
    private LocalDateTime rentTime;
    /**
     * 结束租期
     */
    private LocalDateTime revertTime;

    /**
     * 是否已使用车主券:0.否 1.是
     */
    private Integer couponFlag;

    /**
     * 是否已支付租车押金:0.否 1.是
     */
    private Integer payFlag;

    /**
     * 平台取消原因对应的code值
     */
    private String plateCode;


}
