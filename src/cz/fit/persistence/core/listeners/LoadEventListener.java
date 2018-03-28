package cz.fit.persistence.core.listeners;

import cz.fit.persistence.core.events.EventType;
import cz.fit.persistence.core.events.LoadEntityEvent;
import cz.fit.persistence.exceptions.PersistenceException;

public class LoadEventListener extends AbstractEventListener<LoadEventListener> {
    public LoadEventListener() {
        super(EventType.LOAD);
    }

    public void doLoad(LoadEntityEvent event) throws PersistenceException {

    }

    @Override
    public LoadEventListener getEventListener() {
        return this;
    }
}
