package cz.vutbr.fit.nodbpersistence.core;

import cz.vutbr.fit.nodbpersistence.core.events.EventTypeToListener;
import cz.vutbr.fit.nodbpersistence.core.listeners.AbstractEventListener;

import java.util.EventListener;
import java.util.HashMap;
import java.util.Map;

/**
 * Registry of all existing event listeners.
 */
class ListenerRegistry {

    private final Map<Class<? extends AbstractEventListener>, AbstractEventListener> listeners = new HashMap<>();

    /**
     * Registers given listener and its class to the internal listeners cache.
     * @param listenerClass class of listener
     * @param listener listener
     * @param <T> parameter of listener class must extend {@link AbstractEventListener}
     */
    public <T extends AbstractEventListener> void registerListener(Class<T> listenerClass, T listener) {
        listeners.put(listenerClass, listener);
    }

    /**
     * Finds a listener for given event. It uses {@link EventTypeToListener} to find out the listener
     * assigned to the event.
     * @param eventType object of event type-listener definition
     * @param <T> class of listener
     * @return listener
     */
    @SuppressWarnings({"unchecked"})
    <T extends AbstractEventListener> T findListener(EventTypeToListener<T> eventType) {
        return (T) listeners.get(eventType.getListenerClass());
    }
}
