package com.atzuche.order.accountrenterdeposit.exception;


import com.atzuche.order.accountrenterdeposit.enums.ErrorCode;
import com.atzuche.order.commons.OrderException;
import lombok.Data;

@Data
public class PayOrderRenterDepositDBException extends OrderException {


    public PayOrderRenterDepositDBException() {
        super(ErrorCode.CHANGE_ACCOUT_RENTET_DEPOSIT_FAIL.getCode(),ErrorCode.CHANGE_ACCOUT_RENTET_DEPOSIT_FAIL.getCode());
    }
}
