package com.atzuche.order.accountrenterwzdepost.enums;

import lombok.Getter;

@Getter
public enum ErrorCode {
    ACCOUT_RENTET_WZ_DEPOSIT_FAIL("971001","违章押金操作失败"),
    PAYEN_ORDER_ACCOUT_RENTET_DEPOSIT_FAIL("961002","支付车俩押金成功回调操作失败"),
    CHANGE_ACCOUT_RENTET_DEPOSIT_FAIL("971003","违章押金资金进出操作失败"),


    ;

    private String code;
    private String text;

    private ErrorCode(String code, String text) {
        this.code = code;
        this.text = text;
    }
}
