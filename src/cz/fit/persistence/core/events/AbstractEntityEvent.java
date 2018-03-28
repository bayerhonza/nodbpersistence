package cz.fit.persistence.core.events;

import cz.fit.persistence.core.PersistenceManager;

public abstract class AbstractEntityEvent<T> {

    private Object object;
    private final PersistenceManager source;
    private final EventType entityType;

    AbstractEntityEvent(Object object, PersistenceManager source, EventType type) {
        this.object = object;
        this.source = source;
        this.entityType = type;
    }

    public abstract T getEvent();

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public EventType getEntityType() {
        return entityType;
    }

    public PersistenceManager getSource() {
        return source;
    }
}
