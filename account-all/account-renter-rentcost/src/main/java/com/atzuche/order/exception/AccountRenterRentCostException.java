package com.atzuche.order.exception;

import com.autoyol.commons.web.ErrorCode;
import lombok.Data;

@Data
public class AccountRenterRentCostException extends RuntimeException {
    private String code;
    private String text;
    public AccountRenterRentCostException(String resMsg) {
        super(resMsg);
    }

    public AccountRenterRentCostException(ErrorCode errorCode) {
        super(errorCode.getText());
        this.code=errorCode.getCode();
        this.text=errorCode.getText();
    }
}
