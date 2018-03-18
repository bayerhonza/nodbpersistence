package cz.fit.persistence.core.events;

import cz.fit.persistence.core.listeners.AbstractEventListener;
import cz.fit.persistence.core.listeners.LoadEventListener;
import cz.fit.persistence.core.listeners.PersistEventListener;
import cz.fit.persistence.core.listeners.UpdateEventListener;

public final class EventTypeToListener<T extends AbstractEventListener> {
    public static EventTypeToListener<LoadEventListener> LOAD_EVENT = new EventTypeToListener<>(EventType.LOAD, LoadEventListener.class);
    public static EventTypeToListener<PersistEventListener> PERSIST_EVENT = new EventTypeToListener<>(EventType.PERSIST, PersistEventListener.class);
    public static EventTypeToListener<UpdateEventListener> UPDATE_EVENT = new EventTypeToListener<>(EventType.UPDATE, UpdateEventListener.class);

    private Class<T> listenerClass;
    private EventType eventType;

    private EventTypeToListener(EventType eventType, Class<T> listenerClass) {
        this.listenerClass = listenerClass;
        this.eventType = eventType;
    }

    public Class<T> getListenerClass() {
        return listenerClass;
    }

    public EventType getEventType() {
        return eventType;
    }
}
