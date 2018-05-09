package cz.vutbr.fit.nodbpersistence.core.listeners;

import cz.vutbr.fit.nodbpersistence.annotations.ObjectId;
import cz.vutbr.fit.nodbpersistence.core.PersistenceManager;
import cz.vutbr.fit.nodbpersistence.core.events.LoadEntityEvent;
import cz.vutbr.fit.nodbpersistence.core.klass.manager.AbstractClassManager;
import cz.vutbr.fit.nodbpersistence.exceptions.PersistenceException;

public class LoadEventListener extends AbstractEventListener {

    public Object doLoad(LoadEntityEvent loadEvent) throws PersistenceException {
        PersistenceManager sourcePersistPersistenceManager = loadEvent.getSource();
        AbstractClassManager classManager = sourcePersistPersistenceManager.getContext().findClassManager(loadEvent.getLoadedClass());
        sourcePersistPersistenceManager.getContext().refreshAllStaticFields();
        Object result = classManager.performLoad(loadEvent.getObjectId());
        return result;
    }
}
