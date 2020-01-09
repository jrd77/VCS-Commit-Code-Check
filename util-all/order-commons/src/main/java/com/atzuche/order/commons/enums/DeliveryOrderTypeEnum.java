package com.atzuche.order.commons.enums;

import lombok.Getter;

/*
 * @Author ZhangBin
 * @Date 2020/1/9 11:55
 * @Description: 配送订单type枚举
 *
 **/
@Getter
public enum DeliveryOrderTypeEnum {
    GET_CAR(1,"取车订单"),
    RETURN_CAR(2,"还车订单"),
    ;

    private int code;

    private String name;

    DeliveryOrderTypeEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }
}
