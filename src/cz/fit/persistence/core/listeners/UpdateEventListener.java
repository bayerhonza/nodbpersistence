package cz.fit.persistence.core.listeners;

import cz.fit.persistence.core.events.AbstractEntityEvent;
import cz.fit.persistence.core.events.EventType;
import cz.fit.persistence.core.events.UpdateEntityEvent;
import cz.fit.persistence.exceptions.PersistenceException;

public class UpdateEventListener extends AbstractEventListener {

    public UpdateEventListener() {
        super(EventType.UPDATE);
    }

    public void doUpdate(UpdateEntityEvent event) throws PersistenceException {

    }
}
