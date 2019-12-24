package com.atzuche.order.commons.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 业务类型; 1下单支付收银台,2 收银台退款
 */
@Getter
@AllArgsConstructor
public enum RabbitBusinessTypeEnum {
    ORDER_PAY_CALL_BACK(1,"下单支付收银台"),
    ORDER_REFUND_CALL_BACK(2,"收银台退款"),
    ;

    /**
     * 业务类型编码
     */
    private int code;

    /**
     * 业务类型描述
     */
    private String msg;


}
