package com.atzuche.order.commons.entity.orderDetailDto;

import com.autoyol.doc.annotation.AutoDocProperty;

public class AccountOwnerIncomeListDTO{
    @AutoDocProperty(value="会员号")
    private String memNo;
    @AutoDocProperty(value="收益总金额")
    private Integer incomeAmt;

    public String getMemNo() {
        return memNo;
    }

    public void setMemNo(String memNo) {
        this.memNo = memNo;
    }

    public Integer getIncomeAmt() {
        return incomeAmt;
    }

    public void setIncomeAmt(Integer incomeAmt) {
        this.incomeAmt = incomeAmt;
    }
}
