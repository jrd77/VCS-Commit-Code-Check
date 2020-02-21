package com.atzuche.order.commons.enums.account.income;

import lombok.Getter;

@Getter
public enum AccountOwnerIncomeExamineType {
    OWNER_INCOME(1,"结算收益"),
    OWNER_ADJUSTMENT(2,"调账收益"),
    ;
    private int status;
    private String desc;

    AccountOwnerIncomeExamineType(int status, String desc){
        this.status = status;
        this.desc = desc;
    }



}
