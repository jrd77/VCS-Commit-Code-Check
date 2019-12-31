package com.atzuche.order.coreapi.submitOrder.exception;

/*
 * @Author ZhangBin
 * @Date 2019/12/12 16:20
 * @Description: 取消订单次数校验异常类
 *
 **/
public class CancleOrderCountException extends SubmitOrderException {

    public CancleOrderCountException(String errorCode, String errorMsg) {
        super(errorCode,errorMsg);
    }

}
