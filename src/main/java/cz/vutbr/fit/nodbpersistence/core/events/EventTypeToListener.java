package cz.vutbr.fit.nodbpersistence.core.events;

import cz.vutbr.fit.nodbpersistence.core.listeners.AbstractEventListener;
import cz.vutbr.fit.nodbpersistence.core.listeners.LoadEventListener;
import cz.vutbr.fit.nodbpersistence.core.listeners.PersistEventListener;
import cz.vutbr.fit.nodbpersistence.core.listeners.UpdateEventListener;

public final class EventTypeToListener<T extends AbstractEventListener> {
    public static final EventTypeToListener<LoadEventListener> LOAD_EVENT = new EventTypeToListener<>(EventType.LOAD, LoadEventListener.class);
    public static final EventTypeToListener<PersistEventListener> PERSIST_EVENT = new EventTypeToListener<>(EventType.PERSIST, PersistEventListener.class);
    public static final EventTypeToListener<UpdateEventListener> UPDATE_EVENT = new EventTypeToListener<>(EventType.UPDATE, UpdateEventListener.class);

    private final Class<T> listenerClass;
    private final EventType eventType;

    private EventTypeToListener(EventType eventType, Class<T> listenerClass) {
        this.listenerClass = listenerClass;
        this.eventType = eventType;
    }

    public Class<T> getListenerClass() {
        return listenerClass;
    }
}
