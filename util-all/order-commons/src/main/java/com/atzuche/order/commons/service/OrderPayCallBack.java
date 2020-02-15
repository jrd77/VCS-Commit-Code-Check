package com.atzuche.order.commons.service;

import com.atzuche.order.commons.enums.YesNoEnum;

/**
 *
 */
public interface OrderPayCallBack {
    public void callBack(String menNo,String orderNo, String renterOrderNo, Integer isPayAgain, YesNoEnum isGetCar);

    public void callBackSettle(String orderNo,String renterOrderNo);
}
