package com.atzuche.order.commons.exceptions;

import com.atzuche.order.commons.OrderException;
import com.atzuche.order.commons.enums.ErrorCode;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/3/6 10:33 上午
 **/
public class WzDetailDeleteErrException extends OrderException {
    public WzDetailDeleteErrException() {
        super(ErrorCode.WZ_DETAIL_DEL_ERR.getCode(), ErrorCode.WZ_DETAIL_DEL_ERR.getText());
    }
}
