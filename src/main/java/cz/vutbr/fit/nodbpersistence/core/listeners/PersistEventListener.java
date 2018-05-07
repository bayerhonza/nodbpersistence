package cz.vutbr.fit.nodbpersistence.core.listeners;

import cz.vutbr.fit.nodbpersistence.core.PersistenceManager;
import cz.vutbr.fit.nodbpersistence.core.events.PersistEntityEvent;
import cz.vutbr.fit.nodbpersistence.core.klass.manager.AbstractClassManager;
import cz.vutbr.fit.nodbpersistence.exceptions.PersistenceException;

public class PersistEventListener extends AbstractEventListener {

    public void doPersist(PersistEntityEvent persistEvent) throws PersistenceException {
        PersistenceManager sourcePersistPersistenceManager = persistEvent.getSource();
        AbstractClassManager classManager = sourcePersistPersistenceManager.getContext().findClassManager(persistEvent.getObject().getClass());
        classManager.performPersist(persistEvent);
    }
}
