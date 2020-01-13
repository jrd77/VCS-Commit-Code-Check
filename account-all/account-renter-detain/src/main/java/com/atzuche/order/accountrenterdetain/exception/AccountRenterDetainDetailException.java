package com.atzuche.order.accountrenterdetain.exception;

import com.atzuche.order.commons.OrderException;
import com.atzuche.order.commons.enums.ErrorCode;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class AccountRenterDetainDetailException extends OrderException {


    public AccountRenterDetainDetailException() {
        super(ErrorCode.ACCOUT_RENTER_DETAIL_DETAIL.getCode(),ErrorCode.ACCOUT_RENTER_DETAIL_DETAIL.getCode());
    }
}
