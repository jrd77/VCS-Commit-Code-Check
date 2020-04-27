package com.atzuche.order.sms.service;

import com.atzuche.order.mq.common.base.OrderMessage;
import com.atzuche.order.sms.common.annatation.OrderService;
import com.atzuche.order.sms.common.annatation.Push;
import com.atzuche.order.sms.common.annatation.SMS;
import com.atzuche.order.sms.interfaces.IOrderRouteKeyMessage;
import org.springframework.amqp.core.Message;

import java.util.Map;

/**
 * @author 胡春林
 * 发送订单支付成功事件 （租车费用）
 */
@OrderService
public class OrderPayRentCostSuccessService implements IOrderRouteKeyMessage<Map> {

    @Override
    @SMS(renterFlag = "SELF_SUPPORT_RENT_DEPOSIT_PAID_NOTICE", ownerFlag = "PayRentCarDeposit2Owner")
    @Push(renterFlag = "287", ownerFlag = "10")
    public OrderMessage sendOrderMessageWithNo(OrderMessage orderMessage) {
        return null;
    }


    @Override
    public Map hasSMSElseOtherParams(Map map) {
        return null;
    }

    @Override
    public Map hasPushElseOtherParams(Map map) {
        return null;
    }

}
