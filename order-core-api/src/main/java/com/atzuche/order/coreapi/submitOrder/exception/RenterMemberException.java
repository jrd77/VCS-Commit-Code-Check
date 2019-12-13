package com.atzuche.order.coreapi.submitOrder.exception;

import com.autoyol.commons.web.ErrorCode;
/*
 * @Author ZhangBin
 * @Date 2019/12/13 16:08
 * @Description: 租客获取会员信息异常类
 *
 **/
public class RenterMemberException extends SubmitOrderException  {
    public RenterMemberException(String msg) {
        super(msg);
    }

    public RenterMemberException(ErrorCode errorCode) {
        super(errorCode);
    }

    public RenterMemberException(ErrorCode errorCode, String msg) {
        super(errorCode, msg);
    }
}
