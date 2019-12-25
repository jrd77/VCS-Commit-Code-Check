package common.event.handler;

import common.event.AsyncEventPublish;
import org.springframework.stereotype.Component;

/**
 * @author 胡春林
 */
@Component
public class HandoverCarAsyncEventPublish extends AsyncEventPublish<HandoverCarRoutesEvent> {
    public HandoverCarAsyncEventPublish(HandoverCarRoutesEvent handoverCarRoutesEvent) { super(handoverCarRoutesEvent); }
}
