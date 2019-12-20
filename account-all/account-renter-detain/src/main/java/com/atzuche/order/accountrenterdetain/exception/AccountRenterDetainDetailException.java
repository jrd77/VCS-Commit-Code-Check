package com.atzuche.order.accountrenterdetain.exception;

import com.atzuche.order.accountrenterdetain.enums.ErrorCode;
import com.atzuche.order.commons.OrderException;
import lombok.Data;

@Data
public class AccountRenterDetainDetailException extends OrderException {


    public AccountRenterDetainDetailException() {
        super(ErrorCode.ACCOUT_RENTER_DETAIL_DETAIL.getCode(),ErrorCode.ACCOUT_RENTER_DETAIL_DETAIL.getCode());
    }
}
