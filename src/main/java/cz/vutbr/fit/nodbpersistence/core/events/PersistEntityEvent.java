package cz.vutbr.fit.nodbpersistence.core.events;

import cz.vutbr.fit.nodbpersistence.core.PersistenceManager;

/**
 * Persist event used to pass the objects and information about them.
 */
public class PersistEntityEvent extends EntityEvent {

    public PersistEntityEvent(Object object, PersistenceManager persistenceManager) {
        super(object, persistenceManager);

    }


}
