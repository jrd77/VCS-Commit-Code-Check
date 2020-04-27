package com.atzuche.order.sms.factory;

import com.atzuche.order.sms.common.base.OrderMessageProxy;
import lombok.Data;

import java.lang.reflect.Proxy;

/**
 * @author 胡春林
 */
@Data
public class OrderMessageProxyFactory {
    private static Class<?> iOrderRouteKeyMessage = null;

    public static <T> T newInstance(Class<T> iOrderRouteKeyMessage) {
        OrderMessageProxy orderMessageProxy = new OrderMessageProxy(iOrderRouteKeyMessage);
        return newInstance(orderMessageProxy);
    }

    private static <T> T newInstance(OrderMessageProxy orderMessageProxy) {
        iOrderRouteKeyMessage = orderMessageProxy.getOrderRouteKeyMessage();
        return (T) Proxy.newProxyInstance(iOrderRouteKeyMessage.getClassLoader(),iOrderRouteKeyMessage.getInterfaces(), orderMessageProxy);
    }
}
