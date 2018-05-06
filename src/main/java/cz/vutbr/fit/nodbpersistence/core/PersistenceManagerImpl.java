package cz.vutbr.fit.nodbpersistence.core;


import cz.vutbr.fit.nodbpersistence.core.events.EventTypeToListener;
import cz.vutbr.fit.nodbpersistence.core.events.LoadEntityEvent;
import cz.vutbr.fit.nodbpersistence.core.events.PersistEntityEvent;
import cz.vutbr.fit.nodbpersistence.core.events.UpdateEntityEvent;
import cz.vutbr.fit.nodbpersistence.core.listeners.LoadEventListener;
import cz.vutbr.fit.nodbpersistence.core.listeners.PersistEventListener;
import cz.vutbr.fit.nodbpersistence.core.listeners.UpdateEventListener;
import cz.vutbr.fit.nodbpersistence.exceptions.PersistenceException;


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
    public <T> T load(Long objectId, Class<T> klazz) throws PersistenceException {
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

    private Object launchUpdateAction(UpdateEntityEvent updateEntityEvent) throws PersistenceException {
        UpdateEventListener eventListener = getPersistenceContext().getListenerToEvent(EventTypeToListener.UPDATE_EVENT);
        return eventListener.doUpdate(updateEntityEvent);
    }

    private PersistenceContext getPersistenceContext() {
        return pmfactory.getContext();
    }

}
