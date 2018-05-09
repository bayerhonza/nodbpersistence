package cz.vutbr.fit.nodbpersistence.core.klass.manager;

public class ObjectWrapper {

    private final Object object;
    private final int hashCode;

    public ObjectWrapper(Object object) {
        this.object = object;
        this. hashCode = System.identityHashCode(object);
    }

    public Object getObject() {
        return object;
    }

    public int getHashCode() {
        return hashCode;
    }
}
