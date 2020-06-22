package com.atzuche.violation.exception;

import com.atzuche.order.commons.OrderException;
import com.atzuche.order.commons.enums.ErrorCode;

public class WzRepeatDataException  extends OrderException {

    public WzRepeatDataException() {
        super(ErrorCode.WZ_REPEAT_DATA_ERR.getCode(), ErrorCode.WZ_REPEAT_DATA_ERR.getText());
    }
}
