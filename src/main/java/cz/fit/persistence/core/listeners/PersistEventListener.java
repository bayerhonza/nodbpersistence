package cz.fit.persistence.core.listeners;

import cz.fit.persistence.core.PersistenceManager;
import cz.fit.persistence.core.events.EventType;
import cz.fit.persistence.core.events.PersistEntityEvent;
import cz.fit.persistence.core.klass.manager.DefaultClassManagerImpl;
import cz.fit.persistence.exceptions.PersistenceException;

public class PersistEventListener extends AbstractEventListener<PersistEventListener> {
    public PersistEventListener() {
        super(EventType.PERSIST);
    }

    public void doPersist(PersistEntityEvent persistEvent) throws PersistenceException {
        PersistenceManager sourcePersistPersistenceManager = persistEvent.getSource();
        DefaultClassManagerImpl classManager = sourcePersistPersistenceManager.getContext().findClassManager(persistEvent.getObject().getClass());
        classManager.performPersist(persistEvent);
    }

    @Override
    public PersistEventListener getEventListener() {
        return this;
    }
}
