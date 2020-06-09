package com.atzuche.order.commons.exceptions;

import com.atzuche.order.commons.OrderException;
import com.atzuche.order.commons.enums.ErrorCode;
import org.apache.commons.lang3.StringUtils;

public class CleanRefoundException extends OrderException {

    public CleanRefoundException(String errMsg) {
        super(ErrorCode.CLEAN_REFOUND_ERR.getCode(), StringUtils.isNotBlank(errMsg)?errMsg:ErrorCode.CLEAN_REFOUND_ERR.getText());
    }
}
