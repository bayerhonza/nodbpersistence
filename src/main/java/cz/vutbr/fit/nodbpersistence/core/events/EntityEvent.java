package cz.vutbr.fit.nodbpersistence.core.events;

import cz.vutbr.fit.nodbpersistence.core.PersistenceManager;

/**
 * Abstract class for persistence events. It contains {@code object} as the persistence target and {@code source}
 * as the source of event.
 */
public abstract class EntityEvent {

    private Object object;
    private final PersistenceManager source;

    EntityEvent(Object object, PersistenceManager source) {
        this.object = object;
        this.source = source;
    }

    /**
     * Getter of object
     *
     * @return object of event
     */
    public Object getObject() {
        return object;
    }

    /**
     * Setter of object
     *
     * @param object object of event
     */
    public void setObject(Object object) {
        this.object = object;
    }

    /**
     * Getter of source of event.
     * @return source of event
     */
    public PersistenceManager getSource() {
        return source;
    }
}
