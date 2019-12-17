package com.atzuche.order.accountrenterdeposit.exception;


import com.atzuche.order.accountrenterdeposit.enums.ErrorCode;
import com.atzuche.order.commons.OrderException;
import lombok.Data;

@Data
public class ChangeRenterDepositDBException extends OrderException {


    public ChangeRenterDepositDBException() {
        super(ErrorCode.PAYEN_ORDER_ACCOUT_RENTET_DEPOSIT_FAIL.getCode(),ErrorCode.PAYEN_ORDER_ACCOUT_RENTET_DEPOSIT_FAIL.getCode());
    }
}
