package cz.fit.persistence.core.klass.manager;

import cz.fit.persistence.core.events.EntityEvent;

public interface ClassManager {

    void performPersist(EntityEvent event);

    void performUpdate(EntityEvent event);

    void performDelete(EntityEvent event);
}
