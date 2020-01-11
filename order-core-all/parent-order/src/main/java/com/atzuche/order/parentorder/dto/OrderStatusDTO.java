package com.atzuche.order.parentorder.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 *
 *
 * @author pengcheng.fu
 * @date 2019/12/24 17:16
 */
@Data
public class OrderStatusDTO {

    /**
     * 主订单号
     */
    private String orderNo;
    /**
     * 租车费用支付状态（待支付、已支付）
     */
    private Integer rentCarPayStatus;
    /**
     * 租车费用退款状态（待退款、已退款）
     */
    private Integer rentCarRefundStatus;
    /**
     * 车辆押金支付状态
     */
    private Integer depositPayStatus;
    /**
     * 车辆押金退款状态
     */
    private Integer depositRefundStatus;
    /**
     * 违章押金支付状态
     */
    private Integer wzPayStatus;
    /**
     * 违章押金退款状态
     */
    private Integer wzRefundStatus;
    /**
     * 违章结算状态
     */
    private Integer wzSettleStatus;
    /**
     * 违章结算时间
     */
    private LocalDateTime wzSettleTime;
    /**
     * 车辆押金结算状态
     */
    private Integer carDepositSettleStatus;
    /**
     * 车辆押金结算时间
     */
    private LocalDateTime carDepositSettleTime;
    /**
     * 费用结算状态
     */
    private Integer settleStatus;
    /**
     * 费用结算时间
     */
    private LocalDateTime settleTime;
    /**
     * 是否支持调度
     */
    private Integer isDispatch;
    /**
     * 调度状态
     */
    private Integer dispatchStatus;
    /**
     * 是否理赔 0-否，1-是
     */
    private Integer isClaims;
    /**
     * 是否暂扣  0-否，1-是
     */
    private Integer isDetain;
    /**
     * 是否违章 0-否，1-是
     */
    private Integer isWz;
    /**
     * 主状态: 待确认,待调度,待支付,待交车,待还车,待结算,待违章处理,待违章结算,已完结,已结束,待理赔处理
     */
    private Integer status;

    /**
     * 创建人
     */
    private String createOp;

    /**
     * 修改人
     */
    private String updateOp;


}
