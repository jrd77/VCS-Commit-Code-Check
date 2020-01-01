package com.atzuche.order.commons.enums;

import lombok.Getter;

@Getter
public enum DispatcherReasonEnum {
    owner_refuse("1","车主主动拒单"),
    timeout("2","超时拒单"),
    owner_cancel("3","车主取消订单")
    ;
    private String code;
    private String name;

    DispatcherReasonEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
