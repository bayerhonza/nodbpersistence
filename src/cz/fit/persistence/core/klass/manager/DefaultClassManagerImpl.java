package cz.fit.persistence.core.klass.manager;

import cz.fit.persistence.core.events.AbstractEntityEvent;

public class DefaultClassManagerImpl<T> implements ClassManager<T> {

    private T persistedClass;

    public DefaultClassManagerImpl(T persistedKlass) {
        this.persistedClass = persistedKlass;
    }

    @Override
    public String getClassName() {
        return persistedClass.getClass().getCanonicalName();
    }

    @Override
    public void performPersist(AbstractEntityEvent event) {
        Object object = event.getObject();
        System.out.println(object.getClass().getCanonicalName());
    }

    @Override
    public void performUpdate(AbstractEntityEvent event) {

    }

    @Override
    public void performDelete(AbstractEntityEvent event) {

    }
}
