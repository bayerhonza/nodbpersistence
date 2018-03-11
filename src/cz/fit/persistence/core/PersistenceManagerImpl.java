package cz.fit.persistence.core;


import cz.fit.persistence.core.events.EntityEvent;
import cz.fit.persistence.core.events.EntityEventType;
import cz.fit.persistence.core.events.PersistEntityEvent;
import cz.fit.persistence.core.listeners.PersistEventListener;
import cz.fit.persistence.exceptions.PersistenceException;


public class PersistenceManagerImpl implements PersistenceManager {

    private PersistenceManagerFactory pmfactory;

    public PersistenceManagerImpl(PersistenceManagerFactory pmfactory) {
        this.pmfactory = pmfactory;
    }

    @Override
    public void persist(Object ob) throws PersistenceException {
        launchPersistAction(new PersistEntityEvent(ob, this));
    }

    @Override
    public void update(Object object) throws PersistenceException {

    }

    @Override
    public void find(Object object) throws PersistenceException {

    }

    private void launchPersistAction(PersistEntityEvent event) throws PersistenceException {
        PersistEventListener persistEventListener = getPersistenceContext().getListenerToEvent(EntityEventType.PERSIST_EVENT);
    }

    private PersistenceContext getPersistenceContext() {
        return pmfactory.getContext();
    }

}
