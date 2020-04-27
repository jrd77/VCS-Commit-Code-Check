package com.atzuche.order.commons.exceptions;

import com.atzuche.order.commons.OrderException;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/2/3 2:37 下午
 **/
public class LongRentGetReturnCarServiceException extends OrderException {
    private final static String ERROR_CODE="500003";
    private final static String ERROR_TEXT="长租订单必须使用取还车服务";

    public LongRentGetReturnCarServiceException(){
        super(ERROR_CODE,ERROR_TEXT);
    }
}
