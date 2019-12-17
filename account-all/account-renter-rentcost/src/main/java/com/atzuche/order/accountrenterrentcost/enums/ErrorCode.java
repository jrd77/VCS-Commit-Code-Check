package com.atzuche.order.accountrenterrentcost.enums;

import lombok.Getter;

@Getter
public enum ErrorCode {
    ACCOUT_RENTER_COST_DETAIL("951001","租车费用明细操作失败"),
    ACCOUT_RENTER_COST_SETTLE("951002","租车费用操作失败"),

    ;

    private String code;
    private String text;

    private ErrorCode(String code, String text) {
        this.code = code;
        this.text = text;
    }
}
