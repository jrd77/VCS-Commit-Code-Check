package com.atzuche.order.ownercost.exception;

import com.atzuche.order.commons.OrderException;

public class OrderPriceAdjustmentReqVOTypeException extends OrderException {

    private static String ERROR_CODE = "111063";

    private static String ERROR_MSG = "租客给车主、车主给租客的调价极其原因  传入的类型错误；";

    public OrderPriceAdjustmentReqVOTypeException(String errorCode, String errorMsg) {
        super(errorCode, errorMsg);
    }

    public OrderPriceAdjustmentReqVOTypeException(String msg){
        super(ERROR_CODE, ERROR_MSG+msg);
    }
}
