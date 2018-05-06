package cz.vutbr.fit.nodbpersistence.core.listeners;

import cz.vutbr.fit.nodbpersistence.core.events.EventType;

public abstract class AbstractEventListener<T> {

    private final EventType handledEventType;

    AbstractEventListener(EventType eventType) {
        this.handledEventType = eventType;
    }

    public EventType getHandledEventType() {
        return handledEventType;
    }

    public abstract T getEventListener();
}
