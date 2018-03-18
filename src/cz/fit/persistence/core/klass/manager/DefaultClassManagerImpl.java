package cz.fit.persistence.core.klass.manager;

import cz.fit.persistence.core.events.AbstractEntityEvent;

public class DefaultClassManagerImpl<T> implements ClassManager<T> {

    private Class<T> persistedClass;

    public DefaultClassManagerImpl(Class<T> persistedKlass) {
        this.persistedClass = persistedKlass;
    }

    @Override
    public String getClassName() {
        return persistedClass.getClass().getCanonicalName();
    }

    public Class<T> getPersistedClass() {
        return persistedClass;
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
