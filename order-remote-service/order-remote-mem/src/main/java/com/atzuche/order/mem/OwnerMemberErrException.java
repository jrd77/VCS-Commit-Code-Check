package com.atzuche.order.mem;

import com.atzuche.order.commons.OrderException;
import com.atzuche.order.commons.enums.ErrorCode;

/*
 * @Author ZhangBin
 * @Date 2019/12/13 16:08
 * @Description: 车主获取会员信息异常类
 *
 **/
public class OwnerMemberErrException extends OrderException {

    public OwnerMemberErrException() {
        super(ErrorCode.FEIGN_OWNER_MEMBER_ERROR.getCode(), ErrorCode.FEIGN_OWNER_MEMBER_ERROR.getText());
    }
}
