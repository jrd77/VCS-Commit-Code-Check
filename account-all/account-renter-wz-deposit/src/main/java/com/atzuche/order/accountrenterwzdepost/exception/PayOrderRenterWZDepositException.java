package com.atzuche.order.accountrenterwzdepost.exception;


import com.atzuche.order.accountrenterwzdepost.enums.ErrorCode;
import com.atzuche.order.commons.OrderException;
import lombok.Data;

@Data
public class PayOrderRenterWZDepositException extends OrderException {


    public PayOrderRenterWZDepositException() {
        super(ErrorCode.CHANGE_ACCOUT_RENTET_DEPOSIT_FAIL.getCode(),ErrorCode.CHANGE_ACCOUT_RENTET_DEPOSIT_FAIL.getCode());
    }
}
