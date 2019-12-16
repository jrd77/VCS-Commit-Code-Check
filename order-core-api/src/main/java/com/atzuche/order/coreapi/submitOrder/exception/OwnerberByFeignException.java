package com.atzuche.order.coreapi.submitOrder.exception;

/*
 * @Author ZhangBin
 * @Date 2019/12/13 16:08
 * @Description: 车主获取会员信息异常类
 *
 **/
public class OwnerberByFeignException extends SubmitOrderException  {

    public OwnerberByFeignException(String errorCode, String errorMsg) {
        super(errorCode, errorMsg);
    }
}
