package com.atzuche.order.coreapi.entity.dto;

import lombok.Data;

/**
 *
 *
 * @author pengcheng.fu
 * @date 2020/3/16 14:46
 */
@Data
public class JudgeDutyResDTO {

    /**
     * 是否通知订单结算
     */
    private Boolean isNoticeSettle;

    /**
     * 是否通知累加节假日取消次数
     */
    private Boolean isNoticeOrderCancelMemHolidayDeduct;



}
