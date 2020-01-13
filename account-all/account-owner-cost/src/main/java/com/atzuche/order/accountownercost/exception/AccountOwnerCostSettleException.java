package com.atzuche.order.accountownercost.exception;

import com.atzuche.order.commons.OrderException;
import com.atzuche.order.commons.enums.ErrorCode;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class AccountOwnerCostSettleException extends OrderException {


    public AccountOwnerCostSettleException() {
        super(ErrorCode.ACCOUT_OWNER_COST_SETTLE.getCode(),ErrorCode.ACCOUT_OWNER_COST_SETTLE.getCode());
    }
}
