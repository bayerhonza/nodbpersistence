package cz.fit.persistence.core.listeners;

import cz.fit.persistence.core.events.EventType;

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
