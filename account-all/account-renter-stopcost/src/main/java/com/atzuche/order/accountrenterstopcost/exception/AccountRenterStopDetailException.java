package com.atzuche.order.accountrenterstopcost.exception;


import com.atzuche.order.accountrenterstopcost.enums.ErrorCode;
import com.atzuche.order.commons.OrderException;
import lombok.Data;

@Data
public class AccountRenterStopDetailException extends OrderException {


    public AccountRenterStopDetailException() {
        super(ErrorCode.ACCOUT_RENTER_STOP_DETAIL.getCode(),ErrorCode.ACCOUT_RENTER_STOP_DETAIL.getCode());
    }
}
