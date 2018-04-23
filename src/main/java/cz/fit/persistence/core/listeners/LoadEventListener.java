package cz.fit.persistence.core.listeners;

import cz.fit.persistence.core.PersistenceManager;
import cz.fit.persistence.core.events.EventType;
import cz.fit.persistence.core.events.LoadEntityEvent;
import cz.fit.persistence.core.klass.manager.DefaultClassManagerImpl;
import cz.fit.persistence.exceptions.PersistenceException;

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
