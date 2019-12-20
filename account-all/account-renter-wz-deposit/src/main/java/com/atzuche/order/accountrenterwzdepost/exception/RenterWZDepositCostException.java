package com.atzuche.order.accountrenterwzdepost.exception;


import com.atzuche.order.accountrenterwzdepost.enums.ErrorCode;
import com.atzuche.order.commons.OrderException;
import lombok.Data;

@Data
public class RenterWZDepositCostException extends OrderException {


    public RenterWZDepositCostException() {
        super(ErrorCode.ACCOUT_RENTET_WZ_COST_FAIL.getCode(),ErrorCode.ACCOUT_RENTET_WZ_COST_FAIL.getCode());
    }
}
