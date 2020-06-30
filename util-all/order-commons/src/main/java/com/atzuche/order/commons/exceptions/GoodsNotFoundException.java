package com.atzuche.order.commons.exceptions;

import com.atzuche.order.commons.OrderException;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/2/7 4:09 下午
 **/
public class GoodsNotFoundException extends OrderException {
    private static final String errorCode="700146";
    private static final String errorMsg="获取租客商品异常";



    public GoodsNotFoundException(){
        super(errorCode,errorMsg);
    }
}
