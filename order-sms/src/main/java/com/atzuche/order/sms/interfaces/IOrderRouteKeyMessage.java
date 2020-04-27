package com.atzuche.order.sms.interfaces;

import com.atzuche.order.mq.common.base.OrderMessage;
import org.springframework.amqp.core.Message;

import java.util.Map;

/**
 * @author 胡春林
 * 订单数据类型
 */
public interface IOrderRouteKeyMessage<T> {

    /**
     * 发送订单短消息数据
     */
    OrderMessage sendOrderMessageWithNo(OrderMessage orderMessage);

    /**
     * 是否有特殊参数(SMS)
     * @param t
     * @return
     */
    Map hasSMSElseOtherParams(T t);

    /**
     * 是否有特殊参数(Push)
     * @param t
     * @return
     */
    Map hasPushElseOtherParams(T t);

}
