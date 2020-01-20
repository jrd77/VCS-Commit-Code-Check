package com.atzuche.order.commons.enums;

import lombok.Getter;

@Getter
public enum OrderTransferSourceEnum {
	
	DISPATCH_TRANSFER(0,"调度换车记录"),
	NORMAL_TRANSFER(1,"普通换车记录"),
	CPIC_TRANSFER(2,"太保换车记录"),
	CREATE_ORDER(3,"下单车辆记录");

    private Integer code;
    private String desc;

    OrderTransferSourceEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
