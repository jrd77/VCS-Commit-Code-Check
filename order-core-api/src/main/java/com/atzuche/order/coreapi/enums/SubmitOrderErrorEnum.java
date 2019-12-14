package com.atzuche.order.coreapi.enums;

public enum  SubmitOrderErrorEnum {
    SUBMIT_ORDER_RENTER_MEMBER_ERR("500100","获取会员信息异常")


    ;


    private String code;
    private String text;

    SubmitOrderErrorEnum(String code, String text) {
        this.code = code;
        this.text = text;
    }

    public String getCode() {
        return code;
    }

    public String getText() {
        return text;
    }
}
