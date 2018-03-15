package cz.fit.persistence.core.listeners;

import cz.fit.persistence.core.events.AbstractEntityEvent;
import cz.fit.persistence.exceptions.PersistenceException;

public class LoadEventListener implements EventListener {

    public LoadEventListener() {
    }

    @Override
    public void doAction(AbstractEntityEvent event) throws PersistenceException {

    }
}
