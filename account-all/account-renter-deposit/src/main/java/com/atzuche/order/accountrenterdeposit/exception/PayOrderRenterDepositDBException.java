package com.atzuche.order.accountrenterdeposit.exception;


import com.atzuche.order.commons.OrderException;
import com.atzuche.order.commons.enums.ErrorCode;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
public class PayOrderRenterDepositDBException extends OrderException {


    public PayOrderRenterDepositDBException() {
        super(ErrorCode.CHANGE_ACCOUT_RENTET_DEPOSIT_FAIL.getCode(),ErrorCode.CHANGE_ACCOUT_RENTET_DEPOSIT_FAIL.getCode());
    }
}
