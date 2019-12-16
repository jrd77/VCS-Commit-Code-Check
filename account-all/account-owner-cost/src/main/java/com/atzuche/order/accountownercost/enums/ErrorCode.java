package com.atzuche.order.accountownercost.enums;

import lombok.Getter;

@Getter
public enum ErrorCode {
    ACCOUT_OWNER_INCOME_SETTLE("931001","车主收益结算出错"),
    ACCOUT_OWNER_INCOME_EXAMINE("931002","车主收益审核出错"),

    ;

    private String code;
    private String text;

    private ErrorCode(String code, String text) {
        this.code = code;
        this.text = text;
    }
}
