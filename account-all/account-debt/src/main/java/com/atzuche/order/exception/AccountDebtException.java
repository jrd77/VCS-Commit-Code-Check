package com.atzuche.order.exception;

import com.autoyol.commons.web.ErrorCode;
import lombok.Data;

@Data
public class AccountDebtException extends RuntimeException {
    private String code;
    private String text;
    public AccountDebtException(String resMsg) {
        super(resMsg);
    }

    public AccountDebtException(ErrorCode errorCode) {
        super(errorCode.getText());
        this.code=errorCode.getCode();
        this.text=errorCode.getText();
    }
}
