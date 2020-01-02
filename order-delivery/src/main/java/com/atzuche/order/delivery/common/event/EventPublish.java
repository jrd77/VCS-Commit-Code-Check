package com.atzuche.order.delivery.common.event;

import com.google.common.eventbus.EventBus;

/**
 * eventBus消息基类
 * @param <T>
 */
public abstract class EventPublish<T> {

    protected String name = "event_publish";

    protected EventBus eventBus = new EventBus(name);

    public abstract void push(T t);
}
