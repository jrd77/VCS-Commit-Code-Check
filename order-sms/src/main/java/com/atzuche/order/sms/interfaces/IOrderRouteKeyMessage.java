package com.atzuche.order.sms.interfaces;

import java.util.Map;

/**
 * @author 胡春林
 * 订单数据类型
 */
public interface IOrderRouteKeyMessage<T> {

    /**
     * 发送订单短消息数据
     */
    void sendOrderMessageWithNo();

    /**
     * 是否有特殊参数
     * @param t
     * @return
     */
    Map hasElseOtherParams(T t);

}
