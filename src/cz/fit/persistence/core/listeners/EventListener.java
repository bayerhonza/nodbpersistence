package cz.fit.persistence.core.listeners;

import cz.fit.persistence.core.events.EntityEvent;
import cz.fit.persistence.exceptions.PersistenceException;

public interface EventListener<T> {

    void doAction(EntityEvent event) throws PersistenceException;
}
