package cz.fit.persistence.core;

import cz.fit.persistence.core.events.AbstractEntityEvent;
import cz.fit.persistence.core.events.EventType;
import cz.fit.persistence.core.events.EventTypeToListener;
import cz.fit.persistence.core.listeners.AbstractEventListener;

import java.util.EventListener;
import java.util.HashMap;
import java.util.Map;

public class RegisteredListeneres {

    private Map<Class<? extends AbstractEventListener>, Object> listeners = new HashMap<>();

    public <T extends AbstractEventListener> void registerListener(Class<T> listenerClass, T listener) {
        listeners.put(listenerClass, listener);
    }

    @SuppressWarnings({"unchecked"})
    public <T extends AbstractEventListener> T findListener(EventTypeToListener<T> eventType) {
        return (T) listeners.get(eventType.getListenerClass());
    }

}
