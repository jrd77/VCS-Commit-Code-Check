package com.atzuche.order.cashieraccount.enums;

import lombok.Getter;

@Getter
public enum ErrorCode {
    CASHIER_REFUND_APPLY("981001","退款申请出错"),
    ;

    private String code;
    private String text;

    private ErrorCode(String code, String text) {
        this.code = code;
        this.text = text;
    }
}
