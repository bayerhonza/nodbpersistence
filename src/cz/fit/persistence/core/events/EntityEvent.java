package cz.fit.persistence.core.events;

public abstract class EntityEvent {

    private Object object;
    public EntityEventType TYPE = EntityEventType.DEFAULT_EVENT;

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
