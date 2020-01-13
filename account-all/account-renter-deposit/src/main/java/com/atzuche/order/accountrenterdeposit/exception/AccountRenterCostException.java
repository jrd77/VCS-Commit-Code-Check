package com.atzuche.order.accountrenterdeposit.exception;


import com.atzuche.order.commons.OrderException;
import com.atzuche.order.commons.enums.ErrorCode;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class AccountRenterCostException extends OrderException {


    public AccountRenterCostException() {
        super(ErrorCode.ACCOUT_RENTET_COST_FAIL.getCode(),ErrorCode.ACCOUT_RENTET_COST_FAIL.getCode());
    }
}
