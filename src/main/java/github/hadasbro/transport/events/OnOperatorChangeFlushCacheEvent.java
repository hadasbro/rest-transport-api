package github.hadasbro.transport.events;

import org.springframework.context.ApplicationEvent;

@SuppressWarnings({"unused", "WeakerAccess"})
public class OnOperatorChangeFlushCacheEvent extends ApplicationEvent {

    private String eventCaller;

    public OnOperatorChangeFlushCacheEvent(Class<?> clazz, String slug) {

        super(clazz);

        setEventCaller(clazz.getName() + ":" + slug);

    }

    public String getEventCaller() {
        return eventCaller;
    }

    public void setEventCaller(String eventCaller) {
        this.eventCaller = eventCaller;
    }

}
