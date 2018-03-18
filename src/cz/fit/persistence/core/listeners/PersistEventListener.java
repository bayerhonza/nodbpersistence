package cz.fit.persistence.core.listeners;

import cz.fit.persistence.core.PersistenceManager;
import cz.fit.persistence.core.events.EventType;
import cz.fit.persistence.core.events.PersistEntityEvent;
import cz.fit.persistence.core.klass.manager.ClassManager;
import cz.fit.persistence.exceptions.PersistenceException;

public class PersistEventListener extends AbstractEventListener {
    public PersistEventListener() {
        super(EventType.PERSIST);
    }

    public void doPersist(PersistEntityEvent event) throws PersistenceException {
        if (event instanceof PersistEntityEvent) {
            PersistEntityEvent persistEvent = (PersistEntityEvent) event;
            PersistenceManager source = persistEvent.getSource();
            ClassManager classManager = source.getContext().findClassManager(event.getObject());


            /*
             * launch persisting process
             */
            System.out.println("hello");
        } else {
            throw new PersistenceException("Bad event for this listener.");
        }
    }
}
