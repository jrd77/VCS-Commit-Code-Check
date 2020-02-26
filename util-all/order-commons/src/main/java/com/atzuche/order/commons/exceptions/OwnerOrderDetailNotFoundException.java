package com.atzuche.order.commons.exceptions;

import com.atzuche.order.commons.OrderException;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/2/7 4:09 下午
 **/
public class OwnerOrderDetailNotFoundException extends OrderException {
    private static final String errorCode="700144";
    private static final String errorMsg="有效子订单异常";



    public OwnerOrderDetailNotFoundException(String orderNo,String ownerMemNo){
        super(errorCode,"订单{"+orderNo+"}下，会员{"+ownerMemNo+"}有效数据不存在");
    }
}
