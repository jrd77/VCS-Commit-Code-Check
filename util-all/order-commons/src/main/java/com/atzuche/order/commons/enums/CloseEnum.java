package com.atzuche.order.commons.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CloseEnum {
    IS_CLOSE(1,"已关闭"),
    NOT_CLOSE(0,"未关闭");

    private int code;
    private String desc;
}
