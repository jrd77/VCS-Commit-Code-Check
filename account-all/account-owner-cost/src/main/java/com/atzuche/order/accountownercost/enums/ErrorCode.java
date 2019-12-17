package com.atzuche.order.accountownercost.enums;

import lombok.Getter;

@Getter
public enum ErrorCode {
    ACCOUT_OWNER_COST_SETTLE("931001","车主结算出错"),


    ;

    private String code;
    private String text;

    private ErrorCode(String code, String text) {
        this.code = code;
        this.text = text;
    }
}
