package com.atzuche.order.accountrenterwzdepost.enums;

import lombok.Getter;

@Getter
public enum ErrorCode {
    ACCOUT_RENTET_WZ_DEPOSIT_FAIL("971001","违章押金操作失败"),
    ACCOUT_RENTET_WZ_COST_FAIL("971002","违章费用操作失败"),
    CHANGE_ACCOUT_RENTET_DEPOSIT_FAIL("971003","违章押金资金进出操作失败"),


    ;

    private String code;
    private String text;

    private ErrorCode(String code, String text) {
        this.code = code;
        this.text = text;
    }
}
