package com.atzuche.order.accountdebt.enums;

import lombok.Getter;

@Getter
public enum ErrorCode {
    ACCOUT_DEBT_DEDUCT_DEBT("921001","抵扣历史欠款出错"),
    ACCOUT_DEBT_INSERT_DEBT("921002","记录历史欠款出错"),
    ;

    private String code;
    private String text;

    private ErrorCode(String code, String text) {
        this.code = code;
        this.text = text;
    }
}
