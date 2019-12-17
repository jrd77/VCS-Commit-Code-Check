package com.atzuche.order.accountrenterrentcost.exception;

import com.atzuche.order.accountrenterrentcost.enums.ErrorCode;
import com.atzuche.order.commons.OrderException;
import lombok.Data;

@Data
public class AccountRenterRentCostDetailException extends OrderException {
    public AccountRenterRentCostDetailException() {
        super(ErrorCode.ACCOUT_RENTER_COST_DETAIL.getCode(), ErrorCode.ACCOUT_RENTER_COST_DETAIL.getCode());
    }
}
