package cz.vutbr.fit.nodbpersistence.core.events;

import cz.vutbr.fit.nodbpersistence.core.listeners.AbstractEventListener;
import cz.vutbr.fit.nodbpersistence.core.listeners.LoadEventListener;
import cz.vutbr.fit.nodbpersistence.core.listeners.PersistEventListener;
import cz.vutbr.fit.nodbpersistence.core.listeners.UpdateEventListener;

/**
 * Class for assigning an event type to its listener. This code is based on the principle seen in Hibernate's
 * {@code org.hibernate.event.spi.EventType}.
 *
 * @param <T> event listener class.
 */
public final class EventTypeToListener<T extends AbstractEventListener> {
    public static final EventTypeToListener<LoadEventListener> LOAD_EVENT = new EventTypeToListener<>(LoadEventListener.class);
    public static final EventTypeToListener<PersistEventListener> PERSIST_EVENT = new EventTypeToListener<>(PersistEventListener.class);
    public static final EventTypeToListener<UpdateEventListener> UPDATE_EVENT = new EventTypeToListener<>(UpdateEventListener.class);

    private final Class<T> listenerClass;

    private EventTypeToListener(Class<T> listenerClass) {
        this.listenerClass = listenerClass;
    }

    /**
     * Getter of listener class
     *
     * @return class of listener
     */
    public Class<T> getListenerClass() {
        return listenerClass;
    }
}
