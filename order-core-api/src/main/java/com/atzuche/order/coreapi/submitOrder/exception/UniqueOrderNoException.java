package com.atzuche.order.coreapi.submitOrder.exception;

import com.atzuche.order.commons.OrderException;
import com.atzuche.order.commons.enums.ErrorCode;

/**
 * 生成订单号异常
 *
 * @author pengcheng.fu
 * @date 2019/12/24 14:28
 */
public class UniqueOrderNoException extends OrderException {


    private static final long serialVersionUID = 5017027787368782856L;


    public UniqueOrderNoException(String errorCode, String errorMsg) {
        super(errorCode, errorMsg);
    }

    public UniqueOrderNoException() {
        super(ErrorCode.ORDER_RENTER_ORDERNO_CREATE_ERROR.getCode(), ErrorCode.ORDER_RENTER_ORDERNO_CREATE_ERROR.getText());
    }
}
