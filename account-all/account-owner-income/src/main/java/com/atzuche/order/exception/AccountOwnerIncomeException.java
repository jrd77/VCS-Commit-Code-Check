package com.atzuche.order.exception;

import com.autoyol.commons.web.ErrorCode;
import lombok.Data;

@Data
public class AccountOwnerIncomeException extends RuntimeException {
    private String code;
    private String text;
    public AccountOwnerIncomeException(String resMsg) {
        super(resMsg);
    }

    public AccountOwnerIncomeException(ErrorCode errorCode) {
        super(errorCode.getText());
        this.code=errorCode.getCode();
        this.text=errorCode.getText();
    }
}
