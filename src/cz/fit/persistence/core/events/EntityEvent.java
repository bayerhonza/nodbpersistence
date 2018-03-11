package cz.fit.persistence.core.events;

import cz.fit.persistence.core.PersistenceManager;

public abstract class EntityEvent {

    private Object object;
    private PersistenceManager source;
    public EntityEventType TYPE = EntityEventType.DEFAULT_EVENT;

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
