package com.atzuche.order.sms.service;

import com.atzuche.order.sms.common.annatation.Push;
import com.atzuche.order.sms.common.annatation.SMS;
import com.atzuche.order.sms.interfaces.IOrderRouteKeyMessage;

import java.util.Map;

/**
 * @author 胡春林
 * 发送订单创建成功事件
 */
public class CreateOrderSuccessService implements IOrderRouteKeyMessage<Map> {

    @Override
    @SMS(renterFlag = "",ownerFlag = "")
    @Push(renterFlag = "",ownerFlag = "")
    public void sendOrderMessageWithNo() {

    }

    @Override
    public Map hasElseOtherParams(Map paramsMap) {
        return null;
    }
}
