package cz.fit.persistence.core.events;

import cz.fit.persistence.core.PersistenceManager;

public class LoadEntityEvent extends AbstractEntityEvent<LoadEntityEvent> {
    LoadEntityEvent(Object object, PersistenceManager source) {
        super(object, source);
    }

    @Override
    public LoadEntityEvent getEvent() {
        return null;
    }
}
