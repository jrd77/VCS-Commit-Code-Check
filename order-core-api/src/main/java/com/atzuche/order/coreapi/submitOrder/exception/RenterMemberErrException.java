package com.atzuche.order.coreapi.submitOrder.exception;

import com.atzuche.order.commons.enums.ErrorCode;

/*
 * @Author ZhangBin
 * @Date 2019/12/13 16:08
 * @Description: 租客获取会员信息异常类
 *
 **/
public class RenterMemberErrException extends SubmitOrderException  {

    public RenterMemberErrException() {
        super(ErrorCode.FEIGN_RENTER_MEMBER_ERROR.getCode(),ErrorCode.FEIGN_RENTER_MEMBER_ERROR.getText());
    }
}
