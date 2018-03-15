package cz.fit.persistence.core.events;

import cz.fit.persistence.core.PersistenceManager;

public class PersistEntityEvent extends AbstractEntityEvent<PersistEntityEvent> {

    public PersistEntityEvent(Object object, PersistenceManager persistenceManager) {
        super(object, persistenceManager);

    }

    @Override
    public PersistEntityEvent getEvent() {
        return this;
    }


}
