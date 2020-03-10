package com.atzuche.order.commons.exceptions;

import com.atzuche.order.commons.OrderException;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/3/9 10:43 上午
 **/
public class CarNotFoundException extends OrderException {
    public CarNotFoundException(String errorMsg) {
        super("100005", "车辆不存在:"+errorMsg);
    }
}
