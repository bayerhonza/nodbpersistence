package cz.fit.persistence.core;


import cz.fit.persistence.core.events.EventTypeToListener;
import cz.fit.persistence.core.events.LoadEntityEvent;
import cz.fit.persistence.core.events.PersistEntityEvent;
import cz.fit.persistence.core.listeners.LoadEventListener;
import cz.fit.persistence.core.listeners.PersistEventListener;
import cz.fit.persistence.exceptions.PersistenceException;


public class PersistenceManagerImpl implements PersistenceManager {

    private final PersistenceManagerFactory pmfactory;

    PersistenceManagerImpl(PersistenceManagerFactory pmfactory) {
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
    @SuppressWarnings("unchecked")
    public <T> T load(Integer objectId, Class<T> klazz) throws PersistenceException {
        return (T) launchLoadAction(new LoadEntityEvent(this, objectId, klazz));
    }

    @Override
    public PersistenceContext getContext() {
        return pmfactory.getContext();
    }

    private void launchPersistAction(PersistEntityEvent event) throws PersistenceException {
        PersistEventListener eventListener = getPersistenceContext().getListenerToEvent(EventTypeToListener.PERSIST_EVENT);
        eventListener.doPersist(event);
    }

    private Object launchLoadAction(LoadEntityEvent loadEntityEvent) throws PersistenceException {
        LoadEventListener eventListener = getPersistenceContext().getListenerToEvent(EventTypeToListener.LOAD_EVENT);
        return eventListener.doLoad(loadEntityEvent);
    }

    private PersistenceContext getPersistenceContext() {
        return pmfactory.getContext();
    }

}
