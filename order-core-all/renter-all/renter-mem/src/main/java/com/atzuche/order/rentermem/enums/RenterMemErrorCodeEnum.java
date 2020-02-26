package com.atzuche.order.rentermem.enums;

import lombok.Getter;

@Getter
public enum RenterMemErrorCodeEnum {
    RENTER_MEMBER_RIGHT_CAL_CAR_ERROR("5003001","会员权益-车辆押金计算错误"),
    RENTER_MEMBER_RIGHT_CAL_WZ_ERROR("5003002","会员权益-违章押金计算错误")
    ;

    private String code;
    private String text;

    private RenterMemErrorCodeEnum(String code, String text) {
        this.code = code;
        this.text = text;
    }


}
