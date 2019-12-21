package com.atzuche.order.accountrenterstopcost.enums;

import lombok.Getter;

@Getter
public enum ErrorCode {
    ACCOUT_RENTER_STOP_DETAIL("871001","租客停运费用操作失败");

    private String code;
    private String text;

    private ErrorCode(String code, String text) {
        this.code = code;
        this.text = text;
    }
}
