package cz.fit.persistence.core.events;

import cz.fit.persistence.core.PersistenceManager;

public abstract class AbstractEntityEvent<T> {

    private Object object;
    private PersistenceManager source;

    AbstractEntityEvent(Object object, PersistenceManager source) {
        this.object = object;
        this.source = source;
    }

    public abstract T getEvent();

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
