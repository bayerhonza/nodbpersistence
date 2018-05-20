package cz.vutbr.fit.nodbpersistence.core.listeners;

import cz.vutbr.fit.nodbpersistence.core.PersistenceManager;
import cz.vutbr.fit.nodbpersistence.core.events.PersistEntityEvent;
import cz.vutbr.fit.nodbpersistence.core.klass.manager.AbstractClassManager;

/**
 * Persist event listener.
 */
public class PersistEventListener extends AbstractEventListener {

    /**
     * Handles persist event.
     *
     * @param persistEvent persist event
     */
    public Long doPersist(PersistEntityEvent persistEvent) {
        PersistenceManager sourcePersistPersistenceManager = persistEvent.getSource();
        AbstractClassManager classManager = sourcePersistPersistenceManager.getContext().findClassManager(persistEvent.getObject().getClass());
        return classManager.performPersist(persistEvent);
    }
}
