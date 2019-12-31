package com.atzuche.order.coreapi.submitOrder.exception;

import com.atzuche.order.commons.enums.ErrorCode;

/*
 * @Author ZhangBin
 * @Date 2019/12/13 16:08
 * @Description: 车主获取会员信息异常类
 *
 **/
public class OwnerMemberFailException extends SubmitOrderException  {

    public OwnerMemberFailException() {
        super(ErrorCode.FEIGN_OWNER_MEMBER_FAIL.getCode(), ErrorCode.FEIGN_OWNER_MEMBER_FAIL.getText());
    }
}
