package com.atzuche.order.commons.enums;

import lombok.Getter;

@Getter
public enum  MemberRightValueEnum {
    OWN("1","获得权益"),
    NOT_OWN("0","未获得权益");

    private String code;
    private String name;

    MemberRightValueEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
