package cz.fit.persistence.core.listeners;

import cz.fit.persistence.core.events.AbstractEntityEvent;
import cz.fit.persistence.exceptions.PersistenceException;

public interface EventListener {

    void doAction(AbstractEntityEvent event) throws PersistenceException;

}


