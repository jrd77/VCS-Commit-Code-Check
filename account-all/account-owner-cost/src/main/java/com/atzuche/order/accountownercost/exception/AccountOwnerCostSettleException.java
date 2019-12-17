package com.atzuche.order.accountownercost.exception;

import com.atzuche.order.accountownercost.enums.ErrorCode;
import com.atzuche.order.commons.OrderException;
import lombok.Data;

@Data
public class AccountOwnerCostSettleException extends OrderException {


    public AccountOwnerCostSettleException() {
        super(ErrorCode.ACCOUT_OWNER_COST_SETTLE.getCode(),ErrorCode.ACCOUT_OWNER_COST_SETTLE.getCode());
    }
}
