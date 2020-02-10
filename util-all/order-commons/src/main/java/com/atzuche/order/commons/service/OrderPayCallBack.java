package com.atzuche.order.commons.service;

/**
 *
 */
public interface OrderPayCallBack {
    public void callBack(String orderNo,String renterOrderNo,Integer isPayAgain);

    public void callBackSettle(String orderNo,String renterOrderNo);
}
