package cz.fit.persistence.core.events;

import cz.fit.persistence.core.PersistenceManager;

/**
 * Update event.
 */
public class UpdateEntityEvent extends EntityEvent {
    public UpdateEntityEvent(Object object, PersistenceManager persistenceManager) {
        super(object, persistenceManager);
    }
}
