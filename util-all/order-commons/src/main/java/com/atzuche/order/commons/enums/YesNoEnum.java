package com.atzuche.order.commons.enums;

import lombok.Getter;

@Getter
public enum YesNoEnum {

    YES(0,"正常/是"),
    NO(1,"删除/否"),

    ;
    /**
     * code
     */
    private int code;
    /**
     * msg
     */
    private String msg;

    YesNoEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
