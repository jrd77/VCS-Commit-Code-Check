package com.atzuche.order.sms.service;

import com.atzuche.order.mq.common.base.OrderMessage;
import com.atzuche.order.sms.common.annatation.OrderService;
import com.atzuche.order.sms.common.annatation.SMS;
import com.atzuche.order.sms.interfaces.IOrderRouteKeyMessage;
import org.springframework.amqp.core.Message;

import java.util.Map;

/**
 * @author 胡春林
 * 发送订单取消成功事件
 */
@OrderService
public class CancelOrderSuccessService implements IOrderRouteKeyMessage<Map> {

    @Override
    @SMS(ownerFlag = "PayRentCarIllegalDepositCancelOwner")
    public OrderMessage sendOrderMessageWithNo(Message message) {
return null;
    }

    @Override
    public Map hasElseOtherParams(Map map) {
        return null;
    }
}
