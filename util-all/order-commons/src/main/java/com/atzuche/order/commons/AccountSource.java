package com.atzuche.order.commons;

/**
 * 代表不同的账户，是收款来源时使用
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2019/12/14 10:20 上午
 **/
public enum AccountSource {
    CASHIER(0,"收银台"),
    RENT_ACCOUNT(1,"租金账户"),
    DEPOSIT_ACCOUNT(2,"租车押金账户"),
    WZ_DEPOSIT_ACCOUNT(3,"违章押金账户"),
    DETAIN_ACCOUNT(4,"暂扣费用账户"),
    CLAIM_ACCOUNT(5,"理赔费用账户"),
    WZ_ACCOUNT(6,"违章费用账户"),
    INCOME_CHECK_ACCOUNT(7,"收益待审账户"),
    DEBT_ACCOUNT(8,"欠款账户"),
    REFUND_ACCOUNT(9,"待退款账户");

    private int sourceCode;
    private String sourceDetail;

    AccountSource(int sourceCode, String sourceDetail) {
        this.sourceCode = sourceCode;
        this.sourceDetail = sourceDetail;
    }
}
