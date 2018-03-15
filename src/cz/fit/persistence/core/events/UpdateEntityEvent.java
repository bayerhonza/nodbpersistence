package cz.fit.persistence.core.events;

import cz.fit.persistence.core.PersistenceManager;

public class UpdateEntityEvent extends AbstractEntityEvent<UpdateEntityEvent> {


    public UpdateEntityEvent(Object object, PersistenceManager persistenceManager) {
        super(object, persistenceManager);
    }

    @Override
    public UpdateEntityEvent getEvent() {
        return this;
    }
}
