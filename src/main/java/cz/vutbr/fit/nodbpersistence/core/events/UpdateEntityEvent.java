package cz.vutbr.fit.nodbpersistence.core.events;

import cz.vutbr.fit.nodbpersistence.core.PersistenceManager;

/**
 * Update event.
 */
public class UpdateEntityEvent extends EntityEvent {

    private Long objectId;
    private Class<?> klazz;

    public UpdateEntityEvent(PersistenceManager source, Long objectId, Class<?> klazz) {
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
