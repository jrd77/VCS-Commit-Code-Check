package com.atzuche.order.commons;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum AuthorizeEnum {
    NOT(0,"不是预授权"),
    IS(1,"是预授权"),
    CREDIT(2,"信用支付");

    private int code;
    private String desc;


}
