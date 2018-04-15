package cz.fit.persistence.core.events;

import cz.fit.persistence.core.PersistenceManager;

/**
 * Event for loading objects form persistence. {@code objectId} is the ID parameter of desired object. {@code klazz} is
 * class of new object.
 */
public class LoadEntityEvent extends EntityEvent {

    private Integer objectId;
    private Class<?> klazz;

    public LoadEntityEvent(PersistenceManager source, Integer objectId, Class<?> klazz) {
        super(null, source);
        this.objectId = objectId;
        this.klazz = klazz;
    }

    public Integer getObjectId() {
        return objectId;
    }

    public Class<?> getLoadedClass() {
        return klazz;
    }
}
