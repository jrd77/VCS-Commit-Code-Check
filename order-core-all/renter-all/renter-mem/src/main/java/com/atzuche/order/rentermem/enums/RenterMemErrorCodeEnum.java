package com.atzuche.order.rentermem.enums;

import lombok.Getter;

@Getter
public enum RenterMemErrorCodeEnum {
    RENTER_MEMBER_RIGHT_CAL_ERROR("5003001","车辆押金、违章押金计算错误")

    ;

    private String code;
    private String text;

    private RenterMemErrorCodeEnum(String code, String text) {
        this.code = code;
        this.text = text;
    }


}
