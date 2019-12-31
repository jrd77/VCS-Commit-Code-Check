package com.atzuche.order.delivery.common.event.handler;

import com.atzuche.order.delivery.common.event.AsyncEventPublish;
import org.springframework.context.annotation.Configuration;

/**
 * @author 胡春林
 */
@Configuration
public class HandoverCarAsyncEventPublish extends AsyncEventPublish<HandoverCarRoutesEvent> {
    public HandoverCarAsyncEventPublish(HandoverCarRoutesEvent handoverCarRoutesEvent) { super(handoverCarRoutesEvent); }
}
