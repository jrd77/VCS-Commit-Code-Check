package com.atzuche.order.commons.enums;

import lombok.Getter;

@Getter
public enum SupplementTypeEnum {
	SYSTEM_CREATE(1,"系统创建"),
	MANUAL_CREATE(2,"手动创建");

    private Integer code;
    private String desc;

    SupplementTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
