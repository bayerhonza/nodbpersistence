package cz.vutbr.fit.nodbpersistence.core.events;

import cz.vutbr.fit.nodbpersistence.core.PersistenceManager;

/**
 * General class for persistence events. It contains {@code object} as the persistence target and {@code source}
 * as the source of event.
 */
public abstract class EntityEvent {

    private Object object;
    private final PersistenceManager source;

    EntityEvent(Object object, PersistenceManager source) {
        this.object = object;
        this.source = source;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public PersistenceManager getSource() {
        return source;
    }
}
