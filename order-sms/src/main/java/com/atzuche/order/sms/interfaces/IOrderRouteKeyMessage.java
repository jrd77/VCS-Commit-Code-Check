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
    OrderMessage sendOrderMessageWithNo(Message message);

    /**
     * 是否有特殊参数
     * @param t
     * @return
     */
    Map hasElseOtherParams(T t);

}
