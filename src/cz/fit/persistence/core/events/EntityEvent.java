package cz.fit.persistence.core.events;

public abstract class EntityEvent {

    private Object object;

    EntityEvent(Object object) {
        this.object = object;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }
}
