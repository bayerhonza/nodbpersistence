package cz.vutbr.fit.nodbpersistence.core.events;

import cz.vutbr.fit.nodbpersistence.core.PersistenceManager;

/**
 * Persist event.
 */
public class PersistEntityEvent extends EntityEvent {

    public PersistEntityEvent(Object object, PersistenceManager persistenceManager) {
        super(object, persistenceManager);

    }


}
