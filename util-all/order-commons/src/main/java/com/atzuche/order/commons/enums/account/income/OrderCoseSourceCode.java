package com.atzuche.order.commons.enums.account.income;

import lombok.Getter;

/**
 * 车主收益明细 收益类型
 */
@Getter
public enum OrderCoseSourceCode {
    OWNER_COST_SETTLE(1,"车主结算收益"),
    ;
    private int code;
    private String desc;

    OrderCoseSourceCode(int code, String desc){
        this.code = code;
        this.desc = desc;
    }



}
