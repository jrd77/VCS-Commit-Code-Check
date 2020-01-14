package com.atzuche.order.commons.enums;

import lombok.Getter;

@Getter
public enum YesNoEnum {

    YES(1,"正常/是"),
    NO(0,"删除/否"),

    ;
    /**
     * code
     */
    private Integer code;
    /**
     * msg
     */
    private String msg;

    YesNoEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
