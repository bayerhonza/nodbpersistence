package cz.fit.persistence.core.events;

import cz.fit.persistence.core.PersistenceManager;

/**
 * Persist event.
 */
public class PersistEntityEvent extends EntityEvent {

    public PersistEntityEvent(Object object, PersistenceManager persistenceManager) {
        super(object, persistenceManager);

    }


}
