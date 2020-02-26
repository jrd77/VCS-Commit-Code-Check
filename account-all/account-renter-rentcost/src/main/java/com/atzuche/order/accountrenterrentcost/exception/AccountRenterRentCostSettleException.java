package com.atzuche.order.accountrenterrentcost.exception;

import com.atzuche.order.commons.OrderException;
import com.atzuche.order.commons.enums.ErrorCode;
import lombok.Data;

public class AccountRenterRentCostSettleException extends OrderException {
    public AccountRenterRentCostSettleException() {
        super(ErrorCode.ACCOUT_RENTER_COST_SETTLE.getCode(), ErrorCode.ACCOUT_RENTER_COST_SETTLE.getCode());
    }
}
