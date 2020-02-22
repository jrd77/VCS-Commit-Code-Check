package com.atzuche.order.commons.exceptions;

import com.atzuche.order.commons.OrderException;

/*
 * @Author ZhangBin
 * @Date 2020/2/19 19:00
 * @Description:
 * 
 **/

public class FreeDepositModeException extends OrderException {
    private static final String ERROR_CODE="500231";
    private static final String ERROR_TXT="免押金方式freeDoubleTypeId输入错误";
    public FreeDepositModeException(){
        super(ERROR_CODE,ERROR_TXT);
    }
}
