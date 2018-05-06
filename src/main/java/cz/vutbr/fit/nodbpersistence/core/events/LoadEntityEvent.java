package cz.vutbr.fit.nodbpersistence.core.events;

import cz.vutbr.fit.nodbpersistence.core.PersistenceManager;

/**
 * Event for loading objects form persistence. {@code objectId} is the ID parameter of desired object. {@code klazz} is
 * class of new object.
 */
public class LoadEntityEvent extends EntityEvent {

    private Long objectId;
    private Class<?> klazz;

    public LoadEntityEvent(PersistenceManager source, Long objectId, Class<?> klazz) {
        super(null, source);
        this.objectId = objectId;
        this.klazz = klazz;
    }

    public Long getObjectId() {
        return objectId;
    }

    public Class<?> getLoadedClass() {
        return klazz;
    }
}
