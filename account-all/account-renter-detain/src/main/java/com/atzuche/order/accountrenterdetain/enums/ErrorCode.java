package com.atzuche.order.accountrenterdetain.enums;

import lombok.Getter;

@Getter
public enum ErrorCode {
    ACCOUT_RENTER_DETAIL_DETAIL("891001","暂扣押金失败"),


    ;

    private String code;
    private String text;

    private ErrorCode(String code, String text) {
        this.code = code;
        this.text = text;
    }
}
