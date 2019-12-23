package com.atzuche.order.commons.enums.account.income;

import lombok.Getter;

@Getter
public enum AccountOwnerIncomeExamineStatus {
    WAIT_EXAMINE(1,"待审核"),
    PASS_EXAMINE(2,"审核通过"),
    REFUSE_EXAMINE(3,"审核拒绝"),
    ;
    private int status;
    private String desc;

    AccountOwnerIncomeExamineStatus(int status,String desc){
        this.status = status;
        this.desc = desc;
    }



}
