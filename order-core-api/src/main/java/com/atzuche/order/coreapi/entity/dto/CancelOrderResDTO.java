package com.atzuche.order.coreapi.entity.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 取消订单返回信息
 *
 * @author pengcheng.fu
 * @date 2020/1/7 17:17
 */

@Data
public class CancelOrderResDTO {


    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 租客订单号
     */
    private String renterOrderNo;

    /**
     * 订单城市编码
     */
    private Integer cityCode;

    /**
     * 车辆注册号
     */
    private Integer carNo;

    /**
     * 是否进入调度
     */
    private Boolean isDispatch;

    /**
     * 租车费用支付状态
     */
    private Integer rentCarPayStatus;

    /**
     * 取车服务标识
     */
    private Boolean srvGetFlag;

    /**
     * 还车服务标识
     */
    private Boolean srvReturnFlag;

    /**
     * 订单租期开始时间
     */
    private LocalDateTime rentTime ;

    /**
     * 订单租期截止时间
     */
    private LocalDateTime revertTime ;

    /**
     * 订单状态(最新订单状态)
     */
    private Integer status;

    /**
     * 取消订单责任方
     */
    private Integer wrongdoer;

    /**
     * 车主券编码
     */
    private String ownerCouponNo;

}
