package com.atzuche.order.commons.exceptions;

import com.atzuche.order.commons.OrderException;
import com.atzuche.order.commons.enums.ErrorCode;

public class OilAndMileageException extends OrderException {

    public OilAndMileageException() {
        super(ErrorCode.OIL_MILEAGE_ERR.getCode(), ErrorCode.OIL_MILEAGE_ERR.getText());
    }


}
