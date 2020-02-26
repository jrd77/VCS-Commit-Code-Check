package com.atzuche.order.commons.exceptions;

import com.atzuche.order.commons.OrderException;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/1/31 12:09 下午
 **/
public class InputErrorException extends OrderException {
    private static final String ERROR_CODE="400001";
    private static final String ERROR_TXT="输入错误";

    public InputErrorException(String errorMsg) {
        super(ERROR_CODE, errorMsg);
    }
    public InputErrorException(){
        super(ERROR_CODE,ERROR_TXT);
    }
}
