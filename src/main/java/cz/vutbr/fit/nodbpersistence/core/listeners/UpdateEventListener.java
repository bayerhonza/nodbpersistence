package cz.vutbr.fit.nodbpersistence.core.listeners;

import cz.vutbr.fit.nodbpersistence.core.events.UpdateEntityEvent;
import cz.vutbr.fit.nodbpersistence.exceptions.PersistenceException;

/**
 * Update events listener.
 */
public class UpdateEventListener extends AbstractEventListener {

    /**
     * Handles update events.
     *
     * @param updateEvent update event
     */
    public Object doUpdate(UpdateEntityEvent updateEvent) throws PersistenceException {
        // TODO update event listener
        return null;
    }
}
