package cz.fit.persistence.core.klass.manager;

import cz.fit.persistence.core.events.AbstractEntityEvent;

public interface ClassManager<T> {

    String getClassName();

    void performPersist(AbstractEntityEvent event);

    void performUpdate(AbstractEntityEvent event);

    void performDelete(AbstractEntityEvent event);
}
