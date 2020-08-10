package com.atzuche.order.commons.enums.account.income;

import lombok.Getter;

/**
 * 提现记录状态
 *
 * @author pengcheng.fu
 */
@Getter
public enum AccountOwnerCashExamineStatus {
    /**
     * 0:未处理
     */
    WAIT_PROCESS(0, "未处理"),
    /**
     * 1:已处理
     */
    PASS_PROCESS(1, "已处理"),
    /**
     * 2:暂停
     */
    PAUSE(2, "暂停"),
    /**
     * 11:连接失败
     */
    CONNECTION_FAIL(11, "连接失败"),
    /**
     * 12:打款中
     */
    PAY_ING(12, "打款中"),
    /**
     * 13:打款成功
     */
    PAY_SUCCESS(13, "打款成功"),
    /**
     * 14:打款失败
     */
    PAY_FAIL(14, "打款失败"),
    /**
     * 15:人工处理
     */
    MANUAL_HANDLE(15, "人工处理"),
    ;
    private int status;
    private String desc;

    AccountOwnerCashExamineStatus(int status, String desc) {
        this.status = status;
        this.desc = desc;
    }


}
