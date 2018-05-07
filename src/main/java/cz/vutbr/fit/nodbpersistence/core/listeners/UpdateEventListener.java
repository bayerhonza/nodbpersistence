package cz.vutbr.fit.nodbpersistence.core.listeners;

import cz.vutbr.fit.nodbpersistence.core.PersistenceManager;
import cz.vutbr.fit.nodbpersistence.core.events.UpdateEntityEvent;
import cz.vutbr.fit.nodbpersistence.core.klass.manager.AbstractClassManager;
import cz.vutbr.fit.nodbpersistence.exceptions.PersistenceException;

public class UpdateEventListener extends AbstractEventListener {

    public Object doUpdate(UpdateEntityEvent updateEvent) throws PersistenceException {
        PersistenceManager sourcePersistPersistenceManager = updateEvent.getSource();
        AbstractClassManager classManager = sourcePersistPersistenceManager.getContext().findClassManager(updateEvent.getLoadedClass());
        Long objectId = updateEvent.getObjectId();
        if (classManager.isAlreadyPersisted(objectId)) {
            return classManager.performLoad(updateEvent.getObjectId());
        } else {
            throw new PersistenceException("Object with objectId "+ objectId + " in class " + classManager.getHandledClass().getName() + ".");
        }
    }
}
