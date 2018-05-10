package cz.vutbr.fit.nodbpersistence.core.events;

import cz.vutbr.fit.nodbpersistence.core.PersistenceManager;

/**
 * Event for loading objects form persistence.
 */
public class LoadEntityEvent extends EntityEvent {

    private final Long objectId;
    private final Class<?> klazz;

    public LoadEntityEvent(PersistenceManager source, Long objectId, Class<?> klazz) {
        super(null, source);
        this.objectId = objectId;
        this.klazz = klazz;
    }

    /**
     * Getter for objectId of desired object.
     *
     * @return objectId of desired object
     */
    public Long getObjectId() {
        return objectId;
    }

    /**
     * Getter for desired class
     *
     * @return desired class
     */
    public Class<?> getLoadedClass() {
        return klazz;
    }
}
