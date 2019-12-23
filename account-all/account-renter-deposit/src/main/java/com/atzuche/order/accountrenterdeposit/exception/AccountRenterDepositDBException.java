package com.atzuche.order.accountrenterdeposit.exception;


import com.atzuche.order.commons.OrderException;
import com.atzuche.order.commons.enums.ErrorCode;
import lombok.Data;

@Data
public class AccountRenterDepositDBException extends OrderException {


    public AccountRenterDepositDBException() {
        super(ErrorCode.ACCOUT_RENTET_DEPOSIT_FAIL.getCode(),ErrorCode.ACCOUT_RENTET_DEPOSIT_FAIL.getCode());
    }
}
