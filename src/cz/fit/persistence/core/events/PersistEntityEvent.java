package cz.fit.persistence.core.events;

public class PersistEntityEvent extends EntityEvent {

    public EntityEventType TYPE = EntityEventType.PERSIST_EVENT;

    public PersistEntityEvent(Object object) {
        super(object);

    }


}
