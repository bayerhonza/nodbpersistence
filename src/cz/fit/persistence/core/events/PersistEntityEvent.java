package cz.fit.persistence.core.events;

import cz.fit.persistence.core.PersistenceManager;

public class PersistEntityEvent extends EntityEvent {

    public EntityEventType TYPE = EntityEventType.PERSIST_EVENT;

    public PersistEntityEvent(Object object, PersistenceManager persistenceManager) {
        super(object, persistenceManager);

    }


}
