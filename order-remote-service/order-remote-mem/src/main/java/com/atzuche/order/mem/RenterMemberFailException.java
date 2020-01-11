package com.atzuche.order.mem;

import com.atzuche.order.commons.OrderException;
import com.atzuche.order.commons.enums.ErrorCode;

/*
 * @Author ZhangBin
 * @Date 2019/12/13 16:08
 * @Description: 租客获取会员信息异常类
 *
 **/
public class RenterMemberFailException extends OrderException {

    public RenterMemberFailException() {
        super(ErrorCode.FEIGN_RENTER_MEMBER_FAIL.getCode(),ErrorCode.FEIGN_RENTER_MEMBER_FAIL.getText());
    }
}
