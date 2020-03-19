package com.atzuche.order.commons.service;

import java.util.List;

import com.atzuche.order.commons.enums.YesNoEnum;
import com.autoyol.autopay.gateway.vo.req.NotifyDataVo;

/**
 *
 */
public interface OrderPayCallBack {
    public void callBack(String menNo,String orderNo, String renterOrderNo, Integer isPayAgain, YesNoEnum isGetCar);
    
    //OrderPayCallBackSuccessVO vo
    public void callBack(String menNo,String orderNo, List<String> renterOrderNo, Integer isPayAgain, YesNoEnum isGetCar, List<NotifyDataVo> supplementIds,List<NotifyDataVo> debtIds);
    public void callBackSettle(String orderNo,String renterOrderNo);
}
