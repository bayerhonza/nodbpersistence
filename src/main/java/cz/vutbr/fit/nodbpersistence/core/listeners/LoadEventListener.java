package cz.vutbr.fit.nodbpersistence.core.listeners;

import cz.vutbr.fit.nodbpersistence.core.PersistenceManager;
import cz.vutbr.fit.nodbpersistence.core.events.LoadEntityEvent;
import cz.vutbr.fit.nodbpersistence.core.klass.manager.AbstractClassManager;

/**
 * Load event listener.
 */
public class LoadEventListener extends AbstractEventListener {

    /**
     * Processes load events and loads the object back to memory.
     *
     * @param loadEvent load event
     * @return object to be loaded
     */
    public Object doLoad(LoadEntityEvent loadEvent) {
        PersistenceManager sourcePersistPersistenceManager = loadEvent.getSource();
        AbstractClassManager classManager = sourcePersistPersistenceManager.getContext().findClassManager(loadEvent.getLoadedClass());
        sourcePersistPersistenceManager.getContext().refreshAllStaticFields();
        return classManager.performLoad(loadEvent.getObjectId());
    }
}
