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
    WAIT_PAY(1,"待补付"),
    UPDATE_WAIT_CONFIRM(2,"修改待确认"),
    PROCESS_ING(3,"进行中"),
    FINISH(4,"已完结"),
    END(0,"已结束");

    private int code;
    private String name;

    OwnerChildStatusEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }
}
