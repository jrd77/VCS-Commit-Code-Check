package com.atzuche.order.commons.enums;

import lombok.Getter;


@Getter
public enum SrvGetReturnEnum {

    /**
     * 配送-取车订单
     **/
    SRV_GET_TYPE(1, "取车订单"),
    /**
     * 配送-还车订单
     **/
    SRV_RETURN_TYPE(2, "还车订单");


    private int code;

    private String name;

    SrvGetReturnEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }
}
