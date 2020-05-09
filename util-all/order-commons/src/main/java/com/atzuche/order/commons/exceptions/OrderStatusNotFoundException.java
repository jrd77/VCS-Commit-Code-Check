package com.atzuche.order.commons.exceptions;

import com.atzuche.order.commons.OrderException;
import com.atzuche.order.commons.enums.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/1/30 3:23 下午
 **/
public class OrderStatusNotFoundException extends OrderException {

    public OrderStatusNotFoundException() {
        super(ErrorCode.NOT_FOUNT_ORDER_STATUS_ERR.getCode(), ErrorCode.NOT_FOUNT_ORDER_STATUS_ERR.getText());
    }
}

