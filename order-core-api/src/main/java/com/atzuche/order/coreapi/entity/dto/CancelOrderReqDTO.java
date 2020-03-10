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
     * 使用角色:1.车主 2.租客
     */
    private String memRole;

    /**
     * 取消原因
     */
    private String cancelReason;

    /**
     * 操作人
     */
    private String operatorName;

    /**
     * 是否管理后后台请求: true,是 false,否
     */
    private Boolean consoleInvoke;

    /**
     * 会员注册号
     */
    private String memNo;

}
