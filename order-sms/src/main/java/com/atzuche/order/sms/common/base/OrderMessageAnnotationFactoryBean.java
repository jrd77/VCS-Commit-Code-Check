package com.atzuche.order.sms.common.base;

import com.atzuche.order.sms.factory.OrderMessageProxyFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * @author 胡春林
 * 实现Factorybean  引进代理
 */
public class OrderMessageAnnotationFactoryBean<T> implements FactoryBean<T>,InitializingBean {

    private Class<T> orderRouteKeyMessage = null;

    @Override
    public T getObject() throws Exception {
        return OrderMessageProxyFactory.newInstance(orderRouteKeyMessage);
    }

    @Override
    public Class<?> getObjectType() {
        return orderRouteKeyMessage;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }
}
