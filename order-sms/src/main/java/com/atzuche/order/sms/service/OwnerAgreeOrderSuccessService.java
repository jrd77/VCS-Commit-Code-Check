package com.atzuche.order.sms.service;

import com.atzuche.order.sms.common.annatation.OrderService;
import com.atzuche.order.sms.common.annatation.SMS;
import com.atzuche.order.sms.interfaces.IOrderRouteKeyMessage;

import java.util.Map;

/**
 * @author 胡春林
 * 发送车主同意订单成功事件
 */
@OrderService
public class OwnerAgreeOrderSuccessService implements IOrderRouteKeyMessage<Map> {

    @Override
    @SMS(renterFlag = "NotifyRenterTransReqAccepted")
    public void sendOrderMessageWithNo() {

    }

    @Override
    public Map hasElseOtherParams(Map map) {
        return null;
    }
}
