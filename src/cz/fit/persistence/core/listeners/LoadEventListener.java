package cz.fit.persistence.core.listeners;

import cz.fit.persistence.core.events.AbstractEntityEvent;
import cz.fit.persistence.core.events.EventType;
import cz.fit.persistence.core.events.LoadEntityEvent;
import cz.fit.persistence.exceptions.PersistenceException;
import jdk.jfr.Event;

public class LoadEventListener extends AbstractEventListener {
    public LoadEventListener() {
        super(EventType.LOAD);
    }

    public void doLoad(LoadEntityEvent event) throws PersistenceException {

    }
}
