package cz.vutbr.fit.nodbpersistence.core.listeners;

import cz.vutbr.fit.nodbpersistence.core.PersistenceManager;
import cz.vutbr.fit.nodbpersistence.core.events.EventType;
import cz.vutbr.fit.nodbpersistence.core.events.LoadEntityEvent;
import cz.vutbr.fit.nodbpersistence.core.klass.manager.DefaultClassManagerImpl;
import cz.vutbr.fit.nodbpersistence.exceptions.PersistenceException;

public class LoadEventListener extends AbstractEventListener<LoadEventListener> {
    public LoadEventListener() {
        super(EventType.LOAD);
    }

    public Object doLoad(LoadEntityEvent loadEvent) throws PersistenceException {
        PersistenceManager sourcePersistPersistenceManager = loadEvent.getSource();
        DefaultClassManagerImpl classManager = sourcePersistPersistenceManager.getContext().findClassManager(loadEvent.getLoadedClass());
        return classManager.performLoad(loadEvent.getObjectId());
    }

    @Override
    public LoadEventListener getEventListener() {
        return this;
    }
}
