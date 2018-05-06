package cz.vutbr.fit.nodbpersistence.core.listeners;

import cz.vutbr.fit.nodbpersistence.core.PersistenceManager;
import cz.vutbr.fit.nodbpersistence.core.events.EventType;
import cz.vutbr.fit.nodbpersistence.core.events.UpdateEntityEvent;
import cz.vutbr.fit.nodbpersistence.core.klass.manager.DefaultClassManagerImpl;
import cz.vutbr.fit.nodbpersistence.exceptions.PersistenceException;

public class UpdateEventListener extends AbstractEventListener<UpdateEventListener> {

    public UpdateEventListener() {
        super(EventType.UPDATE);
    }

    public Object doUpdate(UpdateEntityEvent updateEvent) throws PersistenceException {
        PersistenceManager sourcePersistPersistenceManager = updateEvent.getSource();
        DefaultClassManagerImpl classManager = sourcePersistPersistenceManager.getContext().findClassManager(updateEvent.getLoadedClass());
        Long objectId = updateEvent.getObjectId();
        if (classManager.isAlreadyPersisted(objectId)) {
            return classManager.performLoad(updateEvent.getObjectId());
        } else {
            throw new PersistenceException("Object with objectId "+ objectId + " in class " + classManager.getHandledClass().getName() + ".");
        }
    }

    @Override
    public UpdateEventListener getEventListener() {
        return this;
    }
}
