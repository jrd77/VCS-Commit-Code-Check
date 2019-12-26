package com.atzuche.order.accountrenterrentcost.exception;

import com.atzuche.order.commons.OrderException;
import com.atzuche.order.commons.enums.ErrorCode;
import lombok.Data;

@Data
public class AccountRenterRentCostRefundException extends OrderException {
    public AccountRenterRentCostRefundException() {
        super(ErrorCode.ACCOUT_RENTER_COST_SETTLE_REFUND.getCode(), ErrorCode.ACCOUT_RENTER_COST_SETTLE_REFUND.getCode());
    }
}
