package com.atzuche.order.commons.enums;

import lombok.Getter;
/**
 * 修改订单来源方
 */
@Getter
public enum ChangeSourceEnum {

    CONSOLE("1", "后台管理"),
    RENTER("2", "租客"),
    OWNER("3", "车主");

    private String code;

    private String name;

    ChangeSourceEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
