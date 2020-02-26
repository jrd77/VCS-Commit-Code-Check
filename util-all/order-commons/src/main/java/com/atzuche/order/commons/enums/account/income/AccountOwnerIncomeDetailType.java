package com.atzuche.order.commons.enums.account.income;

import lombok.Getter;

/**
 * 车主收益明细 收益类型
 */
@Getter
public enum AccountOwnerIncomeDetailType {
    INCOME(1,"收益"),
    CASH(2,"提现"),
    ;
    private int type;
    private String desc;

    AccountOwnerIncomeDetailType(int type, String desc){
        this.type = type;
        this.desc = desc;
    }



}
