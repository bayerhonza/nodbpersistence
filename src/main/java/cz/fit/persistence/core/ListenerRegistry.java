package cz.fit.persistence.core;

import cz.fit.persistence.core.events.EventTypeToListener;
import cz.fit.persistence.core.listeners.AbstractEventListener;

import java.util.HashMap;
import java.util.Map;

class ListenerRegistry {

    private final Map<Class<? extends AbstractEventListener>, AbstractEventListener> listeners = new HashMap<>();

    <T extends AbstractEventListener> void registerListener(Class<T> listenerClass, T listener) {
        listeners.put(listenerClass, listener);
    }

    @SuppressWarnings({"unchecked"})
    <T extends AbstractEventListener> T findListener(EventTypeToListener<T> eventType) {
        return (T) listeners.get(eventType.getListenerClass());
    }

}
