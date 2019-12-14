package com.atzuche.order.accountownercost.exception;

import com.autoyol.commons.web.ErrorCode;
import lombok.Data;

@Data
public class AccountOwnerCostException extends RuntimeException {
    private String code;
    private String text;
    public AccountOwnerCostException(String resMsg) {
        super(resMsg);
    }

    public AccountOwnerCostException(ErrorCode errorCode) {
        super(errorCode.getText());
        this.code=errorCode.getCode();
        this.text=errorCode.getText();
    }
}
