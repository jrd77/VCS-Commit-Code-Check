package com.atzuche.order.accountrenterclaim.exception;


import com.atzuche.order.commons.OrderException;
import com.atzuche.order.commons.enums.ErrorCode;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class AccountRenterClaimException extends OrderException {


    public AccountRenterClaimException() {
        super(ErrorCode.ACCOUT_RENTER_CLAIM_DETAIL.getCode(),ErrorCode.ACCOUT_RENTER_CLAIM_DETAIL.getCode());
    }
}
