package com.atzuche.delivery.common.event;

import lombok.extern.slf4j.Slf4j;

/**
 * @author 胡春林
 * @param <T>
 */
@Slf4j
public class AsyncEventPublish<T> extends EventPublish {

    public AsyncEventPublish(T t) {
        super.name = "async_event_publish";
        super.eventBus.register(t);
    }

    @Override
    public void push(Object e) {
        log.info("start event publish ...");
        super.eventBus.post(e);
    }
}
