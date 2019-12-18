package com.atzuche.order.accountrenterwzdepost.exception;


import com.atzuche.order.accountrenterwzdepost.enums.ErrorCode;
import com.atzuche.order.commons.OrderException;
import lombok.Data;

@Data
public class AccountRenterWZDepositException extends OrderException {


    public AccountRenterWZDepositException() {
        super(ErrorCode.ACCOUT_RENTET_WZ_DEPOSIT_FAIL.getCode(), ErrorCode.ACCOUT_RENTET_WZ_DEPOSIT_FAIL.getCode());
    }
}
