package com.atzuche.order.coreapi.submitOrder.exception;

/*
 * @Author ZhangBin
 * @Date 2019/12/13 16:08
 * @Description: 租客获取会员信息异常类
 *
 **/
public class RenterDriverFailException extends SubmitOrderException  {

    public RenterDriverFailException(String errorCode, String errorMsg) {
        super(errorCode, errorMsg);
    }
}
