package com.atzuche.order.coreapi.entity.dto;

import lombok.Data;

/**
 * 取消订单处理请求参数
 *
 * @author pengcheng.fu
 * @date 2020/2/25 15:20
 */

@Data
public class CancelOrderReqDTO {

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 租客订单号
     */
    private String renterOrderNo;

    /**
     * 车主订单号
     */
    private String ownerOrderNo;

    /**
     * 会员注册号
     */
    private String memNo;

    /**
     * 使用角色:1.车主 2.租客
     */
    private String memRole;

    /**
     * 取消原因
     */
    private String cancelReason;

    /**
     * 是否申诉(申诉接口):0,否 1,是
     */
    private Integer appealFlag;

    /**
     * 操作人
     */
    private String operatorName;

    /**
     * 是否管理后后台请求: true,是 false,否
     */
    private Boolean consoleInvoke;

    /**
     * 是否收取违约罚金(车主同意取消订单接口):0,否 1,是
     */
    private String takePenalty;

    /**
     * 是否收取违约罚金(车主同意取消订单接口):0,待车主确认 1,已车主确认 2,自动确认
     */
    private Integer refundRecordStatus;


}
