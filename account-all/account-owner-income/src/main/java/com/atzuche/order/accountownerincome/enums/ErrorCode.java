package com.atzuche.order.accountownerincome.enums;

import lombok.Getter;

@Getter
public enum ErrorCode {
    ACCOUT_OWNER_COST_SETTLE("941001","车主收益结算出错"),
    ;

    private String code;
    private String text;

    private ErrorCode(String code, String text) {
        this.code = code;
        this.text = text;
    }
}
