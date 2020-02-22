package com.atzuche.order.commons.exceptions;

import com.atzuche.order.commons.OrderException;

/**
 * @ Author        :  ZhangBin
 * @ CreateDate    :  2019/10/15 10:21
 * @ Description   :  城市圈层异常类
 *
 */

public class CityLonLatException extends OrderException {

    public CityLonLatException(String errorCode, String errorMsg) {
        super(errorCode, errorMsg);
    }
}
