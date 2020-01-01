package com.atzuche.order.commons.enums;

import lombok.Getter;

/*
 * @Author ZhangBin
 * @Date 2019/12/31 17:02
 * @Description:车主子订单状态
 * 
 **/
@Getter
public enum OwnerChildStatusEnum {
    PROCESS_ING(1,"进行中"),
    END(2,"已完结"),
    FINISH(3,"已结束");

    private int code;
    private String name;

    OwnerChildStatusEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }
}
