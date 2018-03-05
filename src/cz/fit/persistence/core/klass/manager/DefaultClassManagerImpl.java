package cz.fit.persistence.core.klass.manager;

import cz.fit.persistence.core.events.EntityEvent;

public class DefaultClassManagerImpl implements ClassManager {


    @Override
    public void performPersist(EntityEvent event) {
        Object object = event.getObject();
        System.out.println(object.getClass().getCanonicalName());
    }

    @Override
    public void performUpdate(EntityEvent event) {

    }

    @Override
    public void performDelete(EntityEvent event) {

    }
}
