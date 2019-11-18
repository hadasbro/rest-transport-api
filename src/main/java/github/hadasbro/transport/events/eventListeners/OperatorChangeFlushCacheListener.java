package github.hadasbro.transport.events.eventListeners;

import github.hadasbro.transport.components.RedisComponent;
import github.hadasbro.transport.events.OnOperatorChangeFlushCacheEvent;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Log
@Component
@SuppressWarnings({"unused"})
public class OperatorChangeFlushCacheListener implements ApplicationListener<OnOperatorChangeFlushCacheEvent> {

    @Autowired
    RedisComponent redis;

    /**
     * onApplicationEvent
     * @param event -
     */
    @Override
    public void onApplicationEvent(@NonNull final OnOperatorChangeFlushCacheEvent event) {

        // flush redis cache
        redis.flush(RedisComponent.KEY_OPERATOR_LIST);

        //log flush cache
        log.info("Flush cache" + event.getEventCaller());

    }

}
