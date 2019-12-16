package com.atzuche.order.coreapi.submitOrder.exception;

import com.autoyol.commons.web.ErrorCode;
/*
 * @Author ZhangBin
 * @Date 2019/12/13 16:08
 * @Description: 租客获取会员信息异常类
 *
 **/
public class RenterMemberByFeignException extends SubmitOrderException  {

    public RenterMemberByFeignException(String errorCode, String errorMsg) {
        super(errorCode, errorMsg);
    }
}
