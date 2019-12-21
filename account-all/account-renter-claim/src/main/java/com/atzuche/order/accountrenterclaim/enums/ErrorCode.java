package com.atzuche.order.accountrenterclaim.enums;

import lombok.Getter;

@Getter
public enum ErrorCode {
    ACCOUT_RENTER_CLAIM_DETAIL("881001","租客理赔费用操作失败");

    private String code;
    private String text;

    private ErrorCode(String code, String text) {
        this.code = code;
        this.text = text;
    }
}
