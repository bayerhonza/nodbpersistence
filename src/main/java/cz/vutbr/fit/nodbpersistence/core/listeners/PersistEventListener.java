package cz.vutbr.fit.nodbpersistence.core.listeners;

import cz.vutbr.fit.nodbpersistence.core.PersistenceManager;
import cz.vutbr.fit.nodbpersistence.core.events.EventType;
import cz.vutbr.fit.nodbpersistence.core.events.PersistEntityEvent;
import cz.vutbr.fit.nodbpersistence.core.klass.manager.DefaultClassManagerImpl;
import cz.vutbr.fit.nodbpersistence.exceptions.PersistenceException;

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
