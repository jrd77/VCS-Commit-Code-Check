package com.atzuche.order.commons.enums.account.income;

import lombok.Getter;

@Getter
public enum AccountOwnerIncomeExamineStatus {
    WAIT_EXAMINE(1,"未审核"),
    PASS_EXAMINE(2,"审核通过"),
    REFUSE_EXAMINE(3,"审核不通过"),
    PASSING_EXAMINE_ERROR(4,"审核中，待核查（异常）"),
    PASSING_EXAMINE_ERROR_TEST(5,"审核中，待核查（测试）"),
    OTHER(99,"其他"),
    ;
    private int status;
    private String desc;

    AccountOwnerIncomeExamineStatus(int status,String desc){
        this.status = status;
        this.desc = desc;
    }



}
