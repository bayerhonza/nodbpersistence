package cz.fit.persistence.core.events;

import cz.fit.persistence.core.PersistenceManager;

public class LoadEntityEvent extends AbstractEntityEvent<LoadEntityEvent> {

    private Integer objectId;
    private Class<?> klazz;

    public LoadEntityEvent(PersistenceManager source, Integer objectId, Class<?> klazz) {
        super(null, source, EventType.LOAD);
        this.objectId = objectId;
        this.klazz = klazz;
    }

    public Integer getObjectId() {
        return objectId;
    }

    public Class<?> getLoadedClass() {
        return klazz;
    }

    @Override
    public LoadEntityEvent getEvent() {
        return this;
    }
}
