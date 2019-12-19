package com.atzuche.order.cashieraccount.enums;

import lombok.Getter;

/**
 * 车辆押金/违章押金退款 状态
 */
@Getter
public enum CashierRefundApplyStatus {
    WAITING_FOR_REFUND(0,"待退款"),
    RECEIVED_REFUND(1,"已退款"),
            ;

    private int code;
    private String text;

    private CashierRefundApplyStatus(int code, String text) {
        this.code = code;
        this.text = text;
    }
}
