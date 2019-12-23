package com.atzuche.order.accountrenterdetain.exception;

import com.atzuche.order.commons.OrderException;
import com.atzuche.order.commons.enums.ErrorCode;
import lombok.Data;

@Data
public class AccountRenterDetainDetailException extends OrderException {


    public AccountRenterDetainDetailException() {
        super(ErrorCode.ACCOUT_RENTER_DETAIL_DETAIL.getCode(),ErrorCode.ACCOUT_RENTER_DETAIL_DETAIL.getCode());
    }
}
