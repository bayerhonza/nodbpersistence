package cz.fit.persistence.core;


import cz.fit.persistence.core.events.PersistEntityEvent;
import cz.fit.persistence.core.listeners.EventListener;
import cz.fit.persistence.exceptions.PersistenceException;


public class PersistenceManagerImpl implements PersistenceManager {

    private PersistenceManagerFactory pmfactory;

    public PersistenceManagerImpl(PersistenceManagerFactory pmfactory) {
        this.pmfactory = pmfactory;
    }

    @Override
    public void persist(Object ob) throws PersistenceException {
        launchPersistAction(new PersistEntityEvent(ob));
    }

    @Override
    public void update(Object object) throws PersistenceException {

    }

    @Override
    public void find(Object object) throws PersistenceException {

    }

    private void launchPersistAction(PersistEntityEvent event) throws PersistenceException {
        EventListener eventListener = getPersistenceContext().getListenerToEvent(event);
    }

    private PersistenceContext getPersistenceContext() {
        return pmfactory.getContext();
    }

}
