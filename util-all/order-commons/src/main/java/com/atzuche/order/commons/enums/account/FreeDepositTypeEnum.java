package com.atzuche.order.commons.enums.account;

import lombok.Getter;

@Getter
public enum FreeDepositTypeEnum {

    BIND_CARD_FREE(1,"绑卡减免"),
    SESAME_FREE(2,"芝麻减免"),
    CONSUME(3,"消费"),
    ;

    /**
     * 支付状态code
     */
    private Integer code;

    /**
     * 支付状态描述
     */
    private String text;

    FreeDepositTypeEnum(Integer code, String text) {
        this.code = code;
        this.text = text;
    }
}
