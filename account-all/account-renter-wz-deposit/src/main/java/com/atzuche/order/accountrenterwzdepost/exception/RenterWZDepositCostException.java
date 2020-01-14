package com.atzuche.order.accountrenterwzdepost.exception;


import com.atzuche.order.commons.OrderException;
import com.atzuche.order.commons.enums.ErrorCode;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
public class RenterWZDepositCostException extends OrderException {


    public RenterWZDepositCostException() {
        super(ErrorCode.ACCOUT_RENTET_WZ_COST_FAIL.getCode(), ErrorCode.ACCOUT_RENTET_WZ_COST_FAIL.getCode());
    }
}
