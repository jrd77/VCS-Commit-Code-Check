package com.atzuche.order.commons.enums.cashier;

import lombok.Getter;

/**
 * 车辆押金/违章押金退款 状态
 */
@Getter
public enum CashierRefundApplyStatus {
    RECEIVED_REFUND("00","已退款"),
    WAITING_FOR_REFUND("01","待退款"),
    STOP_FOR_REFUND("02","暂停退款"),
            ;

    private String code;
    private String text;

    private CashierRefundApplyStatus(String code, String text) {
        this.code = code;
        this.text = text;
    }
}
