package com.atzuche.order.commons.enums;

import lombok.Getter;

/**
 * rabbmitmq 异步事件落库 业务类型
 *
 * @date 2019/12/24 19:32
 */

@Getter
public enum RabbitBusinessTypeEnum {

    ORDER_PAY_CALL_BACK("1", "支付系统回调"),

    OEDER_SETTLE("2", "订单结算"),

    ;


    private String code;

    private String name;

    RabbitBusinessTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }


}


