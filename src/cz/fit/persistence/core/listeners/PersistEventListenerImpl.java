package cz.fit.persistence.core.listeners;

import cz.fit.persistence.core.events.EntityEvent;
import cz.fit.persistence.core.events.PersistEntityEvent;
import cz.fit.persistence.exceptions.PersistenceException;

public class PersistEventListenerImpl implements EventListener{


    @Override
    public void doAction(EntityEvent event) throws PersistenceException {
        if(event instanceof PersistEntityEvent) {
            /**
             * launch persisting process
             */
        }
        else {
            throw new PersistenceException("Bad event for this listener.");
        }
    }
}
