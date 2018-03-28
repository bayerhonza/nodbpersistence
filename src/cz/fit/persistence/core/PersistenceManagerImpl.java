package cz.fit.persistence.core;


import cz.fit.persistence.core.events.EventTypeToListener;
import cz.fit.persistence.core.events.PersistEntityEvent;
import cz.fit.persistence.core.listeners.PersistEventListener;
import cz.fit.persistence.exceptions.PersistenceException;


public class PersistenceManagerImpl implements PersistenceManager {

    private final PersistenceManagerFactory pmfactory;

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

    @Override
    public PersistenceContext getContext() {
        return pmfactory.getContext();
    }

    private void launchPersistAction(PersistEntityEvent event) throws PersistenceException {
        PersistEventListener eventListener = getPersistenceContext().getListenerToEvent(EventTypeToListener.PERSIST_EVENT);
        eventListener.doPersist(event);
    }

    private PersistenceContext getPersistenceContext() {
        return pmfactory.getContext();
    }

}
