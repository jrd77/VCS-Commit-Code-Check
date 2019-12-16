package com.atzuche.order.coreapi.enums;

public enum  SubmitOrderErrorEnum {
    FEIGN_GET_MEMBER_ERROR("500100","获取会员信息异常"),
    FEIGN_GET_RENTER_MEMBER_ERROR("500101","获取租客会员信息异常"),
    FEIGN_GET_RENTER_MEMBER_FAIL("500102","获取租客会员信息失败"),
    FEIGN_GET_OWNER_MEMBER_ERROR("500103","获取车主会员信息异常"),
    FEIGN_GET_OWNER_MEMBER_FAIL("500104","获取车主会员信息失败"),
    FEIGN_GET_CAR_DETAIL_ERROR("500105","获取车辆信息异常"),
    FEIGN_GET_CAR_DETAIL_FAIL("500106","获取车辆信息失败"),



    CANCLE_ORDER_COUNT_ERROR("500200","取消订单次数过多异常"),


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
