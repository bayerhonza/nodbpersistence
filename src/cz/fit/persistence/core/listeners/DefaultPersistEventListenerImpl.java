package cz.fit.persistence.core.listeners;

import cz.fit.persistence.core.events.EntityEvent;
import cz.fit.persistence.exceptions.PersistenceException;

public class DefaultPersistEventListenerImpl implements EventListener{

    @Override
    public void onPersist(EntityEvent event) throws PersistenceException {

    }

    @Override
    public void onUpdate(EntityEvent event) throws PersistenceException {

    }

    @Override
    public void onDelete(EntityEvent event) throws PersistenceException {

    }
}
