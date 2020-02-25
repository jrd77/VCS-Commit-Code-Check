package com.atzuche.order.commons.exceptions;

import com.atzuche.order.commons.OrderException;
/*
 * @Author ZhangBin
 * @Date 2020/2/19 19:00
 * @Description:
 *
 **/
public class PlatformShowException extends OrderException {
    private static final String ERROR_CODE="400040";
    private static final String ERROR_TXT="该车辆暂时无法出租，请租用其他车辆";

    public PlatformShowException(String errorMsg) {
        super(ERROR_CODE, errorMsg);
    }
    public PlatformShowException(){
        super(ERROR_CODE,ERROR_TXT);
    }
}
