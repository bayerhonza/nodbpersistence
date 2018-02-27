package cz.fit.persistence.core.listeners;

import cz.fit.persistence.core.events.EntityEvent;
import cz.fit.persistence.exceptions.PersistenceException;

/**
 * Listener for events coming from persistence
 */
public interface EventListener {

    void onPersist(EntityEvent event) throws PersistenceException;
}
