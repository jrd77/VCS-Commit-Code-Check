package com.atzuche.order.accountrenterwzdepost.exception;


import com.atzuche.order.commons.OrderException;
import com.atzuche.order.commons.enums.ErrorCode;
import lombok.Data;

@Data
public class AccountRenterWZDepositException extends OrderException {


    public AccountRenterWZDepositException() {
        super(ErrorCode.ACCOUT_RENTET_WZ_DEPOSIT_FAIL.getCode(), ErrorCode.ACCOUT_RENTET_WZ_DEPOSIT_FAIL.getCode());
    }
}
